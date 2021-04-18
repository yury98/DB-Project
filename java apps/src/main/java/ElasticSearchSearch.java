import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.ParsedDateHistogram;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.aggregations.metrics.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class ElasticSearchSearch {
    public static void main(String[] args) throws IOException {
        // Установим соединение с сервером
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.56.101", 9200, "http"),
                        new HttpHost("192.168.56.101", 9201, "http")));
        // Запрос 1
        search1(client);
        // Запрос 2
        search2(client);
        // Закроем соединение с сервером
        client.close();
    }

    /**
     * Запрос 1
     * @param client
     * @throws IOException
     */
    private static void search1(RestHighLevelClient client) throws IOException {
        // Создадим запрос к базе с указанием индекса по которому будет идти поиск
        SearchRequest searchRequest = new SearchRequest("subscriber");
        // Создадим экземплять класса для создания тела запроса поиска
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // Создадим агрегацию для запроса
        DateHistogramAggregationBuilder aggregation = AggregationBuilders
                // Агрегация на основе временной гистограммы с именем group_by_month
                .dateHistogram("group_by_month")
                // по полю reg_date
                .field("reg_date")
                // с интегрвалом в месяц
                .calendarInterval(DateHistogramInterval.MONTH)
                // подагрегация
                .subAggregation(AggregationBuilders
                        // Элемент (терм) с именем group_by_profession (группировка)
                        .terms("group_by_profession")
                        // по полю profession
                        .field("profession")
                        // подагрегация
                        .subAggregation(AggregationBuilders
                                // Вложенная агрегация для подсчета количества объектов по пути "comment" с именем count_comments
                                .nested("count_comments", "comment")
                        )
                );
        // Установим тело запроса поика
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);
        // Отправим запрос на сервер
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        /*
         Отобраение ответа
         */
        // Выведем весь ответ в строку
        System.out.println(searchResponse.toString());
        // Достанем агрегации из ответа
        Aggregations aggregations = searchResponse.getAggregations();
        // Достанем агрегацию group_by_month по дате
        ParsedDateHistogram byRegDateAggregation = aggregations.get("group_by_month");
        // Для каждой сущности в ответе выполним
        for (MultiBucketsAggregation.Bucket bucket : byRegDateAggregation.getBuckets()) {
            // Выведем дату (в нашем случае это будет первый день месяца регистрации)
            System.out.println(bucket.getKeyAsString() + " : ");
            // Достанем из подзапроса элемент с именем group_by_month
            Terms byProfessionAggregation = bucket.getAggregations().get("group_by_profession");
            // Для каждой профессии выполним
            for (MultiBucketsAggregation.Bucket bucketProf : byProfessionAggregation.getBuckets()) {
                // Выведем профессию
                System.out.print("    " + bucketProf.getKeyAsString() + " : ");
                // Выведем количество комментариев людей этой профессии
                Nested cnt = bucketProf.getAggregations().get("count_comments");
                System.out.println(cnt.getDocCount());
            }
        }
    }

    /**
     * Запрос 2
     * @param client
     * @throws IOException
     */
    private static void search2(RestHighLevelClient client) throws IOException {
        // Создадим запрос к базе с указанием индекса по которому будет идти поиск
        SearchRequest searchRequest = new SearchRequest("post");
        // Создадим экземплять класса для создания тела запроса поиска
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // Создадим агрегацию для запроса
        TermsAggregationBuilder aggregation = AggregationBuilders
                // По элемену с именем category (операция группировки)
                .terms("category")
                // По полю category
                .field("category");
        // Добавим подагрегацию
        aggregation.subAggregation(AggregationBuilders
                // Сумма с именем view-num
                .sum("view-num")
                // по полю view-num
                .field("view-num")
        );
        // Установим тело запроса поика и отправим запрос на сервер
        searchSourceBuilder.aggregation(aggregation);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        /*
         Отобраение ответа
         */
        // Выведем весь ответ в строку
        System.out.println(searchResponse.toString());
        // Достанем агрегации из ответа
        Aggregations aggregations = searchResponse.getAggregations();
        // Достанем агрегации по категории
        Terms byCategoryAggregation = aggregations.get("category");
        // Для каждой категории выполним
        for (MultiBucketsAggregation.Bucket bucket : byCategoryAggregation.getBuckets()) {
            // Название категории
            System.out.print(bucket.getKeyAsString() + " : ");
            // Суммарное количество просмотров
            Sum sum = bucket.getAggregations().get("view-num");
            System.out.println(sum.getValue());
        }
    }
}
