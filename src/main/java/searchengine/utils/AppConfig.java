package searchengine.utils;

import java.util.Arrays;
import java.util.List;

public class AppConfig {
    private static final String USER_AGENT = "HeliontSearchBot";
    private static final String REFERER = "https://example.com";
    private static final List<String> SITES_TO_CRAWL = Arrays.asList("https://example.com", "https://example2.com");

    public static String getUserAgent() {
        return USER_AGENT;
    }

    public static String getReferrer() {
        return REFERER;
    }

    public static List<String> getSitesToCrawl() {
        return SITES_TO_CRAWL;
    }
}