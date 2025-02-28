package searchengine.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import searchengine.repository.DataBaseRepository;

import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.RecursiveTask;

public class LinkExecutor extends RecursiveTask<String> {
    private final String url;
    private final Integer siteId; // ID сайта, с которым связана страница
    private static final CopyOnWriteArrayList<String> WRITE_ARRAY_LIST = new CopyOnWriteArrayList<>();
    private static final String CSS_QUERY = "a[href]";
    private static final String ATTRIBUTE_KEY = "href";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String REFERRER = "http://www.google.com";
    private static final int DELAY_MS = 500; // Задержка между запросами

    private final DataBaseRepository dataBaseRepository; // Репозиторий для работы с базой данных

    public LinkExecutor(String url, Integer siteId, DataBaseRepository dataBaseRepository) {
        this.url = url.trim();
        this.siteId = siteId;
        this.dataBaseRepository = dataBaseRepository;
    }

    @Override
    public String compute() {
        // Проверяем, была ли уже обработана эта страница
        if (WRITE_ARRAY_LIST.contains(url)) {
            return "";
        }
        WRITE_ARRAY_LIST.add(url); // Помечаем страницу как обработанную

        StringBuffer sb = new StringBuffer();
        List<LinkExecutor> tasks = new CopyOnWriteArrayList<>();

        try {
            // Проверяем MIME-тип содержимого
            if (!isHtmlContent(url)) {
                System.out.println("Skipping non-HTML content: " + url);
                return "";
            }

            // Задержка для избежания блокировки
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException("Thread was interrupted");
            }
            Thread.sleep(DELAY_MS);

            // Получаем содержимое страницы
            Document document = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .get();

            // Сохраняем страницу в базу данных
            savePageToDatabase(document);

            // Ищем все ссылки на странице
            Elements elements = document.select(CSS_QUERY);
            for (Element element : elements) {
                String attributeUrl = element.absUrl(ATTRIBUTE_KEY);
                if (!attributeUrl.isEmpty()
                        && attributeUrl.startsWith(url)
                        && !WRITE_ARRAY_LIST.contains(attributeUrl)
                        && !attributeUrl.contains("#")) {
                    LinkExecutor task = new LinkExecutor(attributeUrl, siteId, dataBaseRepository);
                    task.fork();
                    tasks.add(task);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted while processing URL: " + url);
            return ""; // Прерываем обработку текущего URL
        } catch (IOException e) {
            System.err.println("Error processing URL: " + url + ", Error: " + e.getMessage());
        }

        // Ожидаем завершения всех задач
        tasks.sort(Comparator.comparing((LinkExecutor o) -> o.url));
        for (LinkExecutor task : tasks) {
            sb.append(task.join());
        }

        return sb.toString();
    }

    private boolean isHtmlContent(String url) throws IOException {
        try {
            // Выполняем HEAD-запрос для получения заголовков
            org.jsoup.Connection connection = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .method(org.jsoup.Connection.Method.HEAD);

            // Получаем MIME-тип
            String contentType = connection.execute().header("Content-Type");
            if (contentType == null) {
                return false; // Нет информации о типе содержимого
            }

            // Проверяем, является ли контент HTML
            return contentType.startsWith("text/html") || contentType.contains("xml");
        } catch (Exception e) {
            System.err.println("Failed to check content type for URL: " + url + ", Error: " + e.getMessage());
            return false;
        }
    }

    private void savePageToDatabase(Document document) {
        try {
            // Получаем HTTP-код
            int code = document.connection().response().statusCode();

            // Получаем содержимое страницы
            String content = document.html();

            // Извлекаем корректный path из URL
            URI uri = new URI(document.location());
            String path = uri.getPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            }

            // Сохраняем страницу в базу данных
            dataBaseRepository.insertPage(code, path, content, siteId);
        } catch (Exception e) {
            System.err.println("Failed to save page to database: " + url + ", Error: " + e.getMessage());
        }
    }
}