package searchengine.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.repository.PageRepository;

public class WebExecutor extends RecursiveTask<String> {
    private final String url;
    private final Integer siteId;
    private final PageRepository pageRepository;
    private static final ArrayList<String> VISITED_URLS = new ArrayList<>();
    private static final String CSS_QUERY = "a[href]";
    private static final String ATTRIBUTE_KEY = "href";

    public WebExecutor(String url, Integer siteId, PageRepository pageRepository) {
        this.url = url.trim();
        this.siteId = siteId;
        this.pageRepository = pageRepository;
    }

    @Override
    public String compute() {
        // Проверяем, была ли уже посещена эта страница
        if (VISITED_URLS.contains(url)) {
            return "";
        }
        VISITED_URLS.add(url);

        StringBuffer sb = new StringBuffer();
        List<WebExecutor> tasks = new CopyOnWriteArrayList<>();
        Document document;
        Elements elements;

        try {
            // Задержка для избежания блокировки сервера
            Thread.sleep(150);

            // Подключаемся к странице и получаем HTML-документ
            document = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0").get();
            elements = document.select(CSS_QUERY);

            // Извлекаем путь, код ответа и контент
            String path = getPathFromUrl(url);
            int code = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0").execute().statusCode();
            String content = document.text();

            // Сохраняем данные в таблицу Page
            pageRepository.insertPage(siteId, path, code, content);

            // Обрабатываем все ссылки на странице
            for (Element element : elements) {
                String attributeUrl = element.absUrl(ATTRIBUTE_KEY).trim();

                // Проверяем, что ссылка принадлежит текущему сайту и еще не была посещена
                if (isValidUrl(attributeUrl)){
                    WebExecutor task = new WebExecutor(attributeUrl, siteId, pageRepository);
                    task.fork();
                    tasks.add(task);
                }
            }
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }

        // Собираем результаты выполнения задач
        tasks.sort(Comparator.comparing((WebExecutor o) -> o.url));
        for (WebExecutor task : tasks) {
            sb.append(task.join());
        }

        return sb.toString();
    }

    /**
     * Проверяет, является ли URL допустимым для индексации.
     */
    private boolean isValidUrl(String url) {
        return !url.isEmpty() && url.startsWith(this.url) && !VISITED_URLS.contains(url) && !url.contains("#");
    }

    /**
     * Извлекает путь из URL.
     */
    private String getPathFromUrl(String url) {
        int protocolIndex = url.indexOf("//");
        if (protocolIndex != -1) {
            int pathStartIndex = url.indexOf("/", protocolIndex + 2);
            if (pathStartIndex != -1) {
                return url.substring(pathStartIndex);
            }
        }
        return "/"; // Если путь не указан, возвращаем корневой путь
    }
}