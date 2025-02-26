//package searchengine.utils;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import searchengine.model.Page;
//import searchengine.model.Site;
//import searchengine.repository.DataBaseRepository;
//
//import javax.persistence.*;
//import java.io.IOException;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.RecursiveTask;
//import java.util.concurrent.TimeUnit;
//
//public class LinkExecutor extends RecursiveTask<String> {
//    private final DataBaseRepository repository;
//    private final String url;
//    private final int siteId;
//    private static final String CSS_QUERY = "a[href]";
//    private static final String ATTRIBUTE_KEY = "href";
//    private static final String USER_AGENT = "HeliontSearchBot";
//    private static final String REFERRER = "http://www.google.com";
//    private static final int DELAY_MS = 500; // Задержка между запросами
//
//    public LinkExecutor(DataBaseRepository repository, String url, int siteId) {
//        this.repository = repository;
//        this.url = url.trim();
//        this.siteId = siteId;
//    }
//
//    @Override
//    protected String compute() {
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("YourPersistenceUnit");
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction transaction = em.getTransaction();
//
//        try {
//            // Проверяем, существует ли страница уже в базе данных
//            if (isPageAlreadyProcessed(em, url)) {
//                return "";
//            }
//
//            // Получаем содержимое страницы
//            Document document = Jsoup.connect(url)
//                    .userAgent(USER_AGENT)
//                    .referrer(REFERRER)
//                    .get();
//
//            // Сохраняем страницу в базу данных
//            savePageToDatabase(em, transaction, document);
//
//            // Обновляем статус и время в таблице site
//            updateSiteStatus(em, transaction, "INDEXING");
//
//            // Обрабатываем ссылки на странице
//            List<LinkExecutor> tasks = new ArrayList<>();
//            Elements elements = document.select(CSS_QUERY);
//            for (Element element : elements) {
//                String attributeUrl = element.absUrl(ATTRIBUTE_KEY);
//                if (!attributeUrl.isEmpty() && attributeUrl.startsWith(url) && !isPageAlreadyProcessed(em, attributeUrl)) {
//                    LinkExecutor task = new LinkExecutor(repository, attributeUrl, siteId);
//                    task.fork();
//                    tasks.add(task);
//                    TimeUnit.MILLISECONDS.sleep(DELAY_MS); // Задержка между запросами
//                }
//            }
//
//            // Ожидаем завершения всех задач
//            for (LinkExecutor task : tasks) {
//                task.join();
//            }
//
//            // Обновляем статус на INDEXED после завершения обхода
//            updateSiteStatus(em, transaction, "INDEXED");
//
//        } catch (IOException | InterruptedException e) {
//            // В случае ошибки обновляем статус на FAILED
//            updateSiteStatus(em, transaction, "FAILED");
//            saveErrorToDatabase(em, transaction, e.getMessage());
//            Thread.currentThread().interrupt();
//        } finally {
//            em.close();
//            emf.close();
//        }
//
//        return "";
//    }
//
//    private boolean isPageAlreadyProcessed(EntityManager em, String url) {
//        Query query = em.createQuery("SELECT COUNT(p) FROM Page p WHERE p.path = :path");
//        query.setParameter("path", url);
//        Long count = (Long) query.getSingleResult();
//        return count > 0;
//    }
//
//    private void savePageToDatabase(EntityManager em, EntityTransaction transaction, Document document) {
//        transaction.begin();
//        Page page = new Page();
//        page.setSiteId(siteId);
//        page.setPath(url);
//        page.setCode(200); // Код ответа можно получить из документа Jsoup
//        page.setContent(document.html());
//        em.persist(page);
//        transaction.commit();
//    }
//
//    private void updateSiteStatus(EntityManager em, EntityTransaction transaction, String status) {
//        transaction.begin();
//        Site site = em.find(Site.class, siteId);
//        site.setStatus(SiteStatus.valueOf(status));
//        site.setStatusTime(LocalDateTime.now());
//        em.merge(site);
//        transaction.commit();
//    }
//
//    private void saveErrorToDatabase(EntityManager em, EntityTransaction transaction, String errorMessage) {
//        transaction.begin();
//        Site site = em.find(Site.class, siteId);
//        site.setLastError(errorMessage);
//        em.merge(site);
//        transaction.commit();
//    }
//}