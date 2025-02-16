package searchengine.utils;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.repository.PageRepository;

public class WebExecutor extends RecursiveTask<String> {
    private final String url;
    private static final CopyOnWriteArrayList<String> WRITE_ARRAY_LIST = new CopyOnWriteArrayList<>();
    private static final String CSS_QUERY = "a[href]";
    private static final String ATTRIBUTE_KEY = "href";

    // Добавьте PageRepository как зависимость
    private final PageRepository pageRepository;

    public WebExecutor(String url) {
        this.url = url.trim();
    }

    @Override
    public String compute() {
        // Создание отступов для визуального представления иерархии
        String stringUtils = StringUtils.repeat("\t",
                url.lastIndexOf("/") != url.length() - 1 ? StringUtils.countMatches(url, "/") - 2
                        : StringUtils.countMatches(url, "/") - 3);

        StringBuffer sb = new StringBuffer(String.format("%s%s%s", stringUtils, url, System.lineSeparator()));
        List<WebExecutor> writeArrayList = new CopyOnWriteArrayList<>();
        Document document;
        Elements elements;

        try {
            // Задержка между запросами
            Thread.sleep(150);
            // Получение HTML-документа
            document = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0").get();
            // Извлечение кода ответа (например, 200 для успешного запроса)
            Integer code = document.connection().response().statusCode();
            // Получение содержимого страницы
            String content = document.html();

            // Вставка данных в таблицу Page
            pageRepository.insertPage(url, code, content); // Используйте url как path

            // Выбор всех ссылок на странице
            elements = document.select(CSS_QUERY);
            for (Element element : elements) {
                String attributeUrl = element.absUrl(ATTRIBUTE_KEY);
                // Проверка на уникальность и соответствие базовому URL
                if (!attributeUrl.isEmpty() && !WRITE_ARRAY_LIST.contains(attributeUrl) && !attributeUrl.contains("#")) {
                    // Создание нового экземпляра WebExecutor для каждой найденной ссылки
                    WebExecutor WebExecutor = new WebExecutor(attributeUrl);
                    WebExecutor.fork(); // Запуск задачи асинхронно
                    writeArrayList.add(WebExecutor);
                    WRITE_ARRAY_LIST.add(attributeUrl); // Добавление URL в общий список
                }
            }
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt(); // Восстановление прерванного состояния потока
        }

        // Сортировка задач по URL
        writeArrayList.sort(Comparator.comparing((WebExecutor o) -> o.url));
        int i = 0, allTasksSize = writeArrayList.size();
        while (i < allTasksSize) {
            WebExecutor link = writeArrayList.get(i);
            sb.append(link.join()); // Ожидание завершения задачи и добавление результата
            i++;
        }
        return sb.toString(); // Возврат собранных результатов
    }
}