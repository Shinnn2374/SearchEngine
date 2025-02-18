package searchengine.utils;

import java.io.IOException;
import java.net.URI;
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
    private final String baseUrl; // Базовый URL (домен)
    private final Integer siteId;
    private final PageRepository pageRepository;
    private static final CopyOnWriteArrayList<String> VISITED_URLS = new CopyOnWriteArrayList<>();
    private static final String CSS_QUERY = "a[href]";
    private static final String ATTRIBUTE_KEY = "href";

    public WebExecutor(String url, Integer siteId, PageRepository pageRepository) {
        this.url = url.trim();
        this.baseUrl = extractBaseUrl(url); // Вычисляем базовый URL
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
            Thread.sleep(500);

            // Подключаемся к странице и получаем HTML-документ
            document = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
                    .get();
            elements = document.select(CSS_QUERY);

            // Извлекаем путь, код ответа и контент
            String path = getPathFromUrl(url);
            int code = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0").execute().statusCode();
            String content = document.text();

            // Сохраняем данные в таблицу Page
            pageRepository.insertPage(siteId, path, code, content);

            // Обрабатываем все ссылки на странице
            for (Element element : elements) {
                String rawUrl = element.attr(ATTRIBUTE_KEY).trim();
                String absoluteUrl = resolveAbsoluteUrl(url, rawUrl);

                // Проверяем, что ссылка принадлежит текущему сайту и еще не была посещена
                if (isValidUrl(absoluteUrl)) {
                    WebExecutor task = new WebExecutor(absoluteUrl, siteId, pageRepository);
                    task.fork();
                    tasks.add(task);
                }
            }
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }

        // Собираем результаты выполнения задач
        tasks.sort(Comparator.comparing(o -> o.url));
        for (WebExecutor task : tasks) {
            sb.append(task.join());
        }

        return sb.toString();
    }

    /**
     * Проверяет, является ли URL допустимым для индексации.
     */
    /**
     * Проверяет, является ли URL допустимым для индексации.
     */
    private boolean isValidUrl(String url) {
        if (url.isEmpty() || VISITED_URLS.contains(url) || url.contains("#")) {
            return false;
        }

        try {
            URI candidateUri = new URI(url);
            URI baseUri = new URI(baseUrl);

            // Сравниваем хост и порт для проверки, что это тот же сайт
            return candidateUri.getHost().equals(baseUri.getHost()) &&
                    candidateUri.getPort() == baseUri.getPort();
        } catch (Exception e) {
            return false; // Если URL некорректный, игнорируем его
        }
    }

    /**
     * Преобразует относительную ссылку в абсолютную.
     */
    private String resolveAbsoluteUrl(String base, String relative) {
        if (relative == null || relative.isEmpty()) {
            return "";
        }
        try {
            return URI.create(base).resolve(relative).normalize().toString();
        } catch (IllegalArgumentException e) {
            return "";
        }
    }

    /**
     * Извлекает базовый URL (домен) из указанного URL.
     */
    private String extractBaseUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() + "://" + uri.getHost();
        } catch (Exception e) {
            return "";
        }
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