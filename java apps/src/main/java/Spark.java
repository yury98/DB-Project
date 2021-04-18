import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class Spark {
    public static void main(String[] args) {
        // Создадим сессию со спарком
        SparkSession spark = SparkSession
                // Экземпляр билдера
                .builder()
                // Название приложения
                .appName("Java Spark SQL basic example")
                // Адрес мастера
                .master("spark://192.168.56.101:7077")
                // Создать
                .getOrCreate();
        // Прочитать csv файл с заголовками с постами в датасет
        Dataset<Row> df = spark.read().option("header", "true").csv("hdfs://192.168.56.101:9000/cur/posts.csv");
        // Сохранить датасет во временную таблицу
        df.createOrReplaceTempView("posts");
        // Прочитать csv файл с заголовками с комментариями в датасет
        Dataset<Row> dfCommnets = spark.read().option("header", "true").csv("hdfs://192.168.56.101:9000/cur/comments.csv");
        // Сохранить датасет во временную таблицу
        dfCommnets.createOrReplaceTempView("comments");

        // Запустить sql запрос и сохранить результат в датасет
        Dataset<Row> sqlDF = spark.sql("SELECT p.date AS postDate, COUNT(c.post_id) AS cnt FROM posts p " +
                "FULL JOIN comments c " +
                "ON c.post_id = p.id " +
                "GROUP BY p.date " +
                "ORDER BY cnt DESC");
        // Вывести 25 строк из датасета
        sqlDF.show(25);
    }
}
