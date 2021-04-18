import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

public class Neo4jInput {
    public static Driver driver;

    public static void main(String[] args) throws IOException {
        // Драйвер для подключения к бд
        driver = GraphDatabase.driver( "bolt://192.168.56.102:7687", AuthTokens.basic( "neo4j", "ifirby" ) );
        // Создание сессии
        Session session = driver.session();
        // Удаление всех сущностей из базы
        session.run("MATCH (n)\n DETACH DELETE n");
        // Создадим посты и сохданим их id
        Map<String, String> postIds = createPosts(session);
        // Создадим подписчиков с комментариями
        createSubscribersAndComments(session, postIds);
        // Выведем результат выполнения запроса по заданию
        printResult();
        // Закроем сессию
        session.close();
    }

    /**
     * Вывод результаты выполнения запроса из задания
     */
    public static void printResult()
    {
        // Заголовок таблицы
        System.out.println("Profession : SUM");
        try ( Session session = driver.session() )
        {
            // Начнем транзакцию
            session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    // Выполним в рамках транзакции данный запрос
                    Result result = tx.run( "MATCH (s:Subscriber)-[r:Commented]->(c:Post)\n" +
                                    "RETURN s.profession AS profession, sum(r.amount) AS sum\n" +
                                    "ORDER BY sum DESC" );
                    // Приведем результат к листу для удобства работы
                    List<Record> res = result.list();
                    // Выведем результат
                    for (Record record : res) {
                        System.out.println(String.format("%s : %s", record.get("profession"), record.get("sum")));
                    }
                    return "success";
                }
            } );
        }
    }

    /**
     * Создание постов
     * @param session
     * @return Коллекцию соответствия id поста к id ноды
     * @throws IOException
     */
    private static Map<String, String> createPosts(Session session) throws IOException {
        // Поключение к ES
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.56.101", 9200, "http"),
                        new HttpHost("192.168.56.101", 9201, "http")));
        // Запрос на поиск с ограничением на индекс
        SearchRequest searchRequest = new SearchRequest("post");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // Вырнуть все строки, ограничение на возврат - 25
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(25);
        searchRequest.source(searchSourceBuilder);
        // Отправить запрос
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // Отображение ответа в строку
        System.out.println(searchResponse.toString());
        // Достанем хиты из ответа
        SearchHits postHits = searchResponse.getHits();
        // Создадим пустую коллекцию
        Map<String, String> postIds = new HashMap<>();
        for (int i = 0; i < postHits.getTotalHits().value; i++) {
            // Создадим пост с соответсвующими свойствами (дата, категория, число просмотров)
            String nodeId = createPost(session, postHits.getAt(i).getSourceAsMap().get("date"),
                    postHits.getAt(i).getSourceAsMap().get("category"),
                    postHits.getAt(i).getSourceAsMap().get("view-num")
            );
            // Добавим в коллекцию соответствие
            postIds.put(postHits.getAt(i).getId(), nodeId);
        }
        // Выведем коллекцию
        System.out.println("Post ids: " + postIds);
        System.out.println("**************** Posts created ****************");
        return postIds;
    }

    /**
     * Создание подписчиков и комментариев
     * @param session
     * @param postIds Коллекция соответствия id поста к id ноды
     * @throws IOException
     */
    private static void createSubscribersAndComments(Session session, Map<String, String> postIds) throws IOException {
        // Подключение к ES, создание запроса к индексу и запуск данного запроса
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.56.101", 9200, "http"),
                        new HttpHost("192.168.56.101", 9201, "http")));
        SearchRequest searchRequest = new SearchRequest("subscriber");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(25);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // Отображение ответа в строку
        System.out.println(searchResponse.toString());
        // Получим все хиты из ответа
        SearchHits hits = searchResponse.getHits();
        for (int i = 0; i < hits.getTotalHits().value; i++) {
            // Достанем информацию об образовании. Так как это вложенный тип, то используем Map
            Map education = (Map) hits.getAt(i).getSourceAsMap().get("education");
            // Достанем все комментарии
            ArrayList commentText = (ArrayList) hits.getAt(i).getSourceAsMap().get("comment");
            // Создадим подписчика с соответсвующими свойствами
            String nodeId = createSubscriber(session,
                    hits.getAt(i).getSourceAsMap().get("reg_date"),
                    hits.getAt(i).getSourceAsMap().get("name"),
                    hits.getAt(i).getSourceAsMap().get("birthday"),
                    hits.getAt(i).getSourceAsMap().get("profession"),
                    education.get("speciality")
            );
            // Коллекция соотвествия id поста к количеству комментариев
            Map<String, String> commentAmount = new HashMap<>();
            // Посчитаем количество комментариев к конкретному посту от этого подписчика
            ArrayList<HashMap<String, Object>> commentsUpdated = commentText;
            // Проитерируемся по всем комментариями
            for (HashMap<String, Object> map: commentsUpdated) {
                // Достанем id поста
                String id = String.valueOf(map.get("post_id"));
                int num = 0;
                // Проитерируемся по уже найденным комментариям
                for (HashMap<String, Object> mapPost: commentsUpdated) {
                    // Если такой пост еще найден, то прибавим к num единицу
                    if (id.equals(String.valueOf(mapPost.get("post_id")))) {
                        num++;
                    }
                }
                // Добавляем полученное значение
                commentAmount.put(id, String.valueOf(num));
            }
            // Выведем информацию о подписчике и его комментариях
            System.out.println("Subscriber id " + hits.getAt(i).getId());
            System.out.println("Subscriber nodeId " + nodeId);
            System.out.println(commentAmount);
            // Для каждой сущености в коллекции комметариев
            for (Map.Entry<String, String> entry : commentAmount.entrySet()) {
                // Создадим связь подписчика с постом
                session.run("MATCH (n:Subscriber), (m:Post)\n" +
                    "WHERE id(n) = $subId AND id(m) = $postId\n" +
                    "CREATE (n)-[r:Commented {amount: $amount}]->(m)",
                    parameters("postId", Integer.valueOf(postIds.get(entry.getKey())),
                            "subId", Integer.valueOf(nodeId),
                            "amount", Integer.valueOf(entry.getValue())
                    )
                );
            }
        }
    }

    /**
     * Создать пост
     * @param session
     * @param date дата
     * @param category категория
     * @param viewNum число просмотров
     * @return id ноды
     */
    private static String createPost(Session session, Object date, Object category, Object viewNum) {
        // Начнем новую транзакцию
        String id = session.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute( Transaction tx )
            {
                // Запрос на создание поста с соответствующими параметрами. Возвращается id ноды
                Result result = tx.run( "CREATE (k:Post {date: $date, category: $category, view_num: $view_num})\n" +
                                "RETURN id(k) AS id",
                        parameters("date", date,
                                "category", category,
                                "view_num", viewNum
                        ));
                // Вернем id созданной ноды
                return String.valueOf(result.single().get( 0 ).asInt());
            }
        } );
        return id;
    }

    /**
     *
     * @param session
     * @param regDate Дата регистрации
     * @param name Имя
     * @param birthday Дата рождения
     * @param profession Профессия
     * @param speciality Специальность
     * @return id ноды
     */
    private static String createSubscriber(Session session, Object regDate, Object name, Object birthday, Object profession, Object speciality) {
        // Начнем новую транзакцию
        String id = session.writeTransaction( new TransactionWork<String>()
        {
            @Override
            public String execute( Transaction tx )
            {
                // Запрос на создание подписчика с соответствующими параметрами. Возвращается id ноды
                Result result = tx.run( "CREATE (n:Subscriber {reg_date: $reg_date, name: $name, birthday: $birthday, profession: $profession, speciality: $speciality})\n" +
                                "RETURN id(n) AS id",
                        parameters("reg_date", regDate,
                                "name", name,
                                "birthday", birthday,
                                "profession", profession,
                                "speciality", speciality
                        ));
                // Вернем id созданной ноды
                return String.valueOf(result.single().get( 0 ).asInt());
            }
        } );
        return id;
    }
}
