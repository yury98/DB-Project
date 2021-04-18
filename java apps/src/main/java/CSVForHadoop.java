import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVForHadoop {
    public static void main(String[] args) throws IOException {
        createPostsCSV();
        createSubscribersAndCommentsCSV();
    }

    /**
     * Создает CSV с подписчиками и CSV с комментариями
     * @throws IOException
     */
    private static void createSubscribersAndCommentsCSV() throws IOException {
        // Заголовок для csv подписчиков
        String subHeader = "id,reg_date,name,image_href,birthday,email,profession,education_university,education_end_year,education_description,education_speciality\n";
        // Заголовок для csv коммертариев
        String commentHeader = "sub_id,post_id,date,text\n";
        // Получим всех подписчиков из ES
        SearchHits hits = getAllEntities("subscriber");
        // Пустые строки для подписчиков и комментариев
        StringBuilder subscribers = new StringBuilder();
        StringBuilder comments = new StringBuilder();
        // Для каждой полученной сущности
        for (int i = 0; i < hits.getTotalHits().value; i++) {
            // Получим информацию об образовании подписчика
            Map education = (Map) hits.getAt(i).getSourceAsMap().get("education");
            // Получим комментарии
            ArrayList commentText = (ArrayList) hits.getAt(i).getSourceAsMap().get("comment");
            // ID подписчика
            String subId = hits.getAt(i).getId();
            // Добавим в строку всю инфомрацию о подписчике через запятую
            subscribers.append(subId)
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("reg_date"))
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("name"))
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("image-href"))
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("birthday"))
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("email"))
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("profession"))
                    .append(",")
                    .append(education.get("university"))
                    .append(",")
                    .append(education.get("end-year"))
                    .append(",")
                    .append(escapeSpecialCharacters((String) education.get("description")))
                    .append(",")
                    .append(education.get("speciality"))
                    .append("\n");
            // Преобразуем комментарии в коллекцию, потому что их может быть несколько для каждого подписчика
            ArrayList<HashMap<String, Object>> commentsUpdated = commentText;
            // Добавим в строку всю инфомрацию о комментарии через запятую
            for (HashMap<String, Object> map: commentsUpdated) {
                comments.append(subId)
                        .append(",")
                        .append(map.get("post_id"))
                        .append(",")
                        .append(map.get("date"))
                        .append(",")
                        .append(escapeSpecialCharacters((String) map.get("text")))
                        .append("\n");
            }
        }
        // Сохраним csv файл с подписчиками в корень проекта
        FileWriter subWriter = new FileWriter("subscribers.csv");
        subWriter.write(subHeader + subscribers.toString());
        subWriter.close();
        // Сохраним csv файл с комментариями в корень проекта
        FileWriter commentWriter = new FileWriter("comments.csv");
        commentWriter.write(commentHeader + comments.toString());
        commentWriter.close();
        System.out.println("Subs and comments created");
    }

    /**
     * Создание cvs файла с постами
     * @throws IOException
     */
    private static void createPostsCSV() throws IOException {
        // Заголовки файла
        String header = "id,date,category,text,video_href,image_href,view_num\n";
        FileWriter myWriter = new FileWriter("posts.csv");
        // Получение всех постов из ES
        SearchHits hits = getAllEntities("post");
        StringBuilder posts = new StringBuilder();
        // Для каждого поста заполним параметры через запятую
        for (int i = 0; i < hits.getTotalHits().value; i++) {
            posts.append(hits.getAt(i).getId())
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("date"))
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("category"))
                    .append(",")
                    .append(escapeSpecialCharacters((String) hits.getAt(i).getSourceAsMap().get("text")))
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("video-href"))
                    .append(",")
                    .append(hits.getAt(i).getSourceAsMap().get("image-href"))
                    .append(",")
                    .append(String.valueOf(hits.getAt(i).getSourceAsMap().get("view-num")))
                    .append("\n");
        }
        // Запишем посты в csv файл в корень проекта
        myWriter.write(header + posts.toString());
        myWriter.close();
        System.out.println("Posts created");
    }

    /**
     * Получение данных из ES
     * @param entity Название индекса
     * @return Результат поиска
     * @throws IOException
     */
    private static SearchHits getAllEntities(String entity) throws IOException {
        // Подключение к ES
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.56.101", 9200, "http"),
                        new HttpHost("192.168.56.101", 9201, "http")));
        // Запрос на поиск и его выполнение
        SearchRequest searchRequest = new SearchRequest(entity);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(25);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // Получение ответа
        System.out.println(searchResponse.toString());
        return searchResponse.getHits();
    }

    /**
     * Замена спец символов в строке
     * @param data
     * @return
     */
    private static String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        escapedData = escapedData.replaceAll("\n", " ");
        escapedData = escapedData.replace("\"", " ");
        escapedData = "\"" + escapedData + "\"";
        return escapedData;
    }
}
