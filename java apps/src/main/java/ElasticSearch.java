import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import wrapper.Post;
import wrapper.PostIndex;
import wrapper.SubscriberIndex;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ElasticSearch {
    public static void main(String[] args) throws IOException {
        // Создадим соединение с сервером
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.56.101", 9200, "http"),
                        new HttpHost("192.168.56.101", 9201, "http")));
        // Посты
        deleteIndexPost(client);
        createIndexPost(client);
        addPost(client);
        // Подписчики
        deleteIndexSubscriber(client);
        createIndexSubscriber(client);
        addSubscriber(client);
        // Закрыть соединение с сервером
        client.close();
    }

    /**
     * Создание индекса "пост"
     * @param client
     * @throws IOException
     */
    private static void createIndexPost(RestHighLevelClient client) throws IOException {
        // Создадим запрос для создания индекса
        CreateIndexRequest request = new CreateIndexRequest("post");
        // Добавим маппинг постов типа json
        request.source(getPostProperties(), XContentType.JSON);
        // Создадим индекс
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * Добавление постов в индекс
     * @param client
     * @throws IOException
     */
    private static void addPost(RestHighLevelClient client) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Десереаизируем посты
        PostIndex[] posts = mapper.readValue(getPosts(), PostIndex[].class);
        // Преобразуем массив в лист для удобства работы
        List<PostIndex> postIndexArrayList = Arrays.asList(posts);
        for (PostIndex post : postIndexArrayList) {
            // Создадим запрос в индекс с соответствующим id
            IndexRequest request = new IndexRequest(post.getIndex())
                    .id(post.getId().toString());
            // Вставим тело запроса (непосредственно данные) типа json
            request.source(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(post.getBody()), XContentType.JSON);
            // Отправим запрос на сервер
            client.index(request, RequestOptions.DEFAULT);
        }
    }

    /**
     * Удаление индекса "пост"
     * @param client
     * @throws IOException
     */
    private static void deleteIndexPost(RestHighLevelClient client) throws IOException {
        // Запрос на удаление индекса
        DeleteIndexRequest request = new DeleteIndexRequest("post");
        // Отправим запрос на сервер
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    /**
     * Создание индекса "подписчик"
     * @param client
     * @throws IOException
     */
    private static void createIndexSubscriber(RestHighLevelClient client) throws IOException {
        // Запрос на создание индекса
        CreateIndexRequest request = new CreateIndexRequest("subscriber");
        // Маппинг для подписчиков
        request.source(getSubscriberProperties(), XContentType.JSON);
        // Отправка запроса на создание индекса
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * Добавление подписчиков в индекс
     * @param client
     * @throws IOException
     */
    private static void addSubscriber(RestHighLevelClient client) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        // Десериализация подписчиков
        SubscriberIndex[] subscribers = mapper.readValue(getSubscribers(), SubscriberIndex[].class);
        // Преобразование массива в лист для удобства обработки
        List<SubscriberIndex> subscribersArrayList = Arrays.asList(subscribers);
        for (SubscriberIndex subscriber : subscribersArrayList) {
            // Запрос на добавление документа в индекс с соответствуюим id
            IndexRequest request = new IndexRequest(subscriber.getIndex())
                    .id(subscriber.getId().toString());
            // Добавление тела запроса
            request.source(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(subscriber.getBody()), XContentType.JSON);
            // Отправка запроса на сервер
            client.index(request, RequestOptions.DEFAULT);
        }
    }

    /**
     * Удаление индекса "подписчик"
     * @param client
     * @throws IOException
     */
    private static void deleteIndexSubscriber(RestHighLevelClient client) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("subscriber");
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    /**
     * Чтение файла с маппингом постов
     * @return
     * @throws IOException
     */
    private static String getPostProperties() throws IOException {
        File file = new File("post_properties.json");
        String fileStr = FileUtils.readFileToString(file, "UTF-8");
        return fileStr;
    }

    /**
     * Чтение файла с постами
     * @return
     * @throws IOException
     */
    private static String getPosts() throws IOException {
        File file = new File("posts.json");
        String fileStr = FileUtils.readFileToString(file, "UTF-8");
        return fileStr;
    }

    /**
     * Чтение файла с маппингом подписчиков
     * @return
     * @throws IOException
     */
    private static String getSubscriberProperties() throws IOException {
        File file = new File("subscriber_properties.json");
        String fileStr = FileUtils.readFileToString(file, "UTF-8");
        return fileStr;
    }

    /**
     * Чтение файлов с подписчиками
     * @return
     * @throws IOException
     */
    private static String getSubscribers() throws IOException {
        File file = new File("subscribers.json");
        String fileStr = FileUtils.readFileToString(file, "UTF-8");
        return fileStr;
    }
}
