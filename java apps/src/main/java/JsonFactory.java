import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import wrapper.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JsonFactory {

    // Категории поста
    private static final String[] categories = {"technology", "news", "politics", "education", "cinema"};
    // Университеты
    private static final String[] universities = {"BMSTU", "MGIMO", "MSU", "Oxford", "Stanford"};
    // Имена
    private static final String[] names = {"Yury", "Ann", "Andrew", "John", "Bob", "Alex", "Kate"};
    // Профессии
    private static final String[] professions = {"Doctor", "Artist", "Teacher", "Programmer", "Policeman"};

    public static void main(String[] args) throws IOException {
        createPosts();
        createSubscribers();
    }

    /**
     * Метод для генерации постов
     * @throws IOException
     */
    private static void createPosts() throws IOException {
        // Создадим пустой лист постов
        ArrayList<PostIndex> posts = new ArrayList<>();
        Random rand = new Random();
        // Создадим 25 постов
        for (int i = 0; i < 25; i++) {
            posts.add(
                    // Служебная информация
                    new PostIndex("post", "post", i,
                    new Post(
                            // Дата
                            getRandomDate(),
                            // Категория
                            categories[rand.nextInt(5)],
                            // Текст поста
                            getRandomText(),
                            // Ссылка на видео
                            getRandomUrl(),
                            // Ссылка на изображение
                            getRandomUrl(),
                            // Число просмотров (рандомное число от 0 до 9999)
                            rand.nextInt(10000)
                    )));
        }
        // Маппер для сериализации класса
        ObjectMapper mapper = new ObjectMapper();
        // Преобразование данных в строку
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(posts);
        // Создаем новый файл в директории проекта
        FileWriter myWriter = new FileWriter("posts.json");
        // Запишем в файл json
        myWriter.write(jsonResult);
        // Закроем файл
        myWriter.close();
    }

    /**
     * Метод для генерации подписчиков
     * @throws IOException
     */
    private static void createSubscribers() throws IOException {
        // Создадим пустой лист подписчиков
        ArrayList<SubscriberIndex> subscribers = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 25; i++) {
            // Сгенерируем "образование"
            Education education = new Education(
                    // Университет
                    universities[rand.nextInt(5)],
                    // Специальность
                    professions[rand.nextInt(5)],
                    // Год окончания
                    rand.nextInt(30) + 2000,
                    // Описание специальности
                    getRandomText());
            // Создадим пустой лист комментариев подписчика
            List<Comment> comments = new ArrayList<>();
            // Добавим туда от 0 до 4 комментариев
            for (int j = 0; j < rand.nextInt(5); j++) {
                comments.add(new Comment(
                        // Дата
                        getRandomDate(),
                        // ID поста
                        rand.nextInt(25),
                        // Текст комментария
                        getRandomText()
                ));
            }
            // Добавим подписчика
            subscribers.add(new SubscriberIndex(
                    // Служебная информация
                    "subscriber", "subscriber", i,
                    // Подписчик
                    new Subscriber(
                            // Дата регистрации
                            getRandomDate(),
                            // Имя
                            names[rand.nextInt(7)],
                            // Ссылка на фото
                            getRandomUrl(),
                            // Дата рождения
                            getRandomDate(),
                            // Email
                            getRandomEmail(),
                            // Род занятий
                            professions[rand.nextInt(5)],
                            // Образование
                            education,
                            // Комментарии
                            comments
                    )
            ));
        }
        /*
        Серриализация класса
        Преобразование подписчиков в стоку и затем запись в json файл
         */
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(subscribers);
        FileWriter myWriter = new FileWriter("subscribers.json");
        myWriter.write(jsonResult);
        myWriter.close();
    }

    /**
     * Метод для генерации даты
     * @return дата
     */
    private static String getRandomDate() {
        // Получим текущую дату
        LocalDateTime curDate = LocalDateTime.now();
        Random rand = new Random();
        // Прибавим к текущей дате рандомное количество
        LocalDateTime date = curDate
                // минут (от 0 до 59)
                .plusMinutes(rand.nextInt(60))
                // часов (от 0 до 23)
                .plusHours(rand.nextInt(24))
                // дней (от 0 до 29)
                .plusDays(rand.nextInt(30))
                // месяцев (от 0 до 11)
                .plusMonths(rand.nextInt(12))
                // лет (от 0 до 9)
                .plusYears(rand.nextInt(10));
        // Преобразуем дату к правильному формату, чтобы вернуть строку
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return date.format(formatter);
    }

    /**
     * Метод для генерации текста
     * @return длинный текст
     * @throws IOException
     */
    private static String getRandomText() throws IOException {
        Random rand = new Random();
        // Файл с текстом книги "Академия" Азимова, который лежит в корне проекта
        File file = new File("Foundation Azimov.txt");
        // Читаем файл
        String fileStr = FileUtils.readFileToString(file, "UTF-8");
        // Начинаем с 10000 символа
        int startNum = rand.nextInt(10000);
        // Заканчиваем по формуле : начало + рандомное число от 0 до 10000 + 1000
        int endNum = startNum + rand.nextInt(10000) + 1000;
        // Заменим перенос строки на проблел (для удобства чтения)
        return fileStr.substring(startNum, endNum).replace("\n", " ");
    }

    /**
     * Метод для генерации рандомной ссылки
     * @return url
     */
    private static String getRandomUrl() {
        // Ссылка формата "https://%s.ru", где %s - рандомная строка из 50 символов
        return String.format("https://%s.ru", RandomStringUtils.randomAlphabetic(50));
    }

    /**
     * Метод для генерации рандомного email
     * @return email
     */
    private static String getRandomEmail() {
        // email формата "%s@%s.ru", где первый %s - рандомная строка из 20 символов, второй - рандомная строка из 7 символов
        return String.format("%s@%s.ru", RandomStringUtils.randomAlphabetic(20), RandomStringUtils.randomAlphabetic(7));
    }
}
