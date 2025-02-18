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
    private final Integer siteId;
    private final PageRepository pageRepository;
    private static final CopyOnWriteArrayList<String> WRITE_ARRAY_LIST = new CopyOnWriteArrayList<>();
    private static final String CSS_QUERY = "a[href]";
    private static final String ATTRIBUTE_KEY = "href";

    public WebExecutor(String url, Integer siteId, PageRepository pageRepository) {
        this.url = url.trim();
        this.siteId = siteId;
        this.pageRepository = pageRepository;
    }

    @Override
    public String compute() {
        String stringUtils = StringUtils.repeat("\t",
                url.lastIndexOf("/") != url.length() - 1 ? StringUtils.countMatches(url, "/") - 2
                        : StringUtils.countMatches(url, "/") - 3);

        StringBuffer sb = new StringBuffer(String.format("%s%s%s", stringUtils, url, System.lineSeparator()));
        List<WebExecutor> writeArrayList = new CopyOnWriteArrayList<>();
        Document document;
        Elements elements;
        try {
            Thread.sleep(150);
            document = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0").get();
            elements = document.select(CSS_QUERY);

            // Извлекаем путь
            String path;
            int protocolIndex = url.indexOf("//");
            if (protocolIndex != -1) {
                int pathStartIndex = url.indexOf("/", protocolIndex + 2);
                if (pathStartIndex != -1) {
                    path = url.substring(pathStartIndex);
                } else {
                    path = "/"; // Если путь не указан, используем корневой путь
                }
            } else {
                path = url; // Если URL не содержит протокола, используем весь URL как путь
            }

            // Извлекаем код ответа и контент
            int code = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0").execute().statusCode();
            String content = document.text();

            // Сохраняем данные в таблицу Page
            pageRepository.insertPage(siteId, path, code, content);

            for (Element element : elements) {
                String attributeUrl = element.absUrl(ATTRIBUTE_KEY);
                if (!attributeUrl.isEmpty() && attributeUrl.startsWith(url) && !WRITE_ARRAY_LIST.contains(attributeUrl) && !attributeUrl
                        .contains("#")) {
                    WebExecutor WebExecutor = new WebExecutor(attributeUrl, siteId, pageRepository);
                    WebExecutor.fork();
                    writeArrayList.add(WebExecutor);
                    WRITE_ARRAY_LIST.add(attributeUrl);
                }
            }
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
        }

        writeArrayList.sort(Comparator.comparing((WebExecutor o) -> o.url));
        int i = 0, allTasksSize = writeArrayList.size();
        while (i < allTasksSize) {
            WebExecutor link = writeArrayList.get(i);
            sb.append(link.join());
            i++;
        }
        return sb.toString();
    }
}