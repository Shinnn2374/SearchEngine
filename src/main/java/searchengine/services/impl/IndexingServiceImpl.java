package searchengine.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.repository.SiteRepository;
import searchengine.repository.PageRepository;
import searchengine.services.IndexingService;
import searchengine.utils.WebExecutor;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final SitesList sitesList;
    private Integer siteId = 0;

    private final AtomicBoolean isIndexing = new AtomicBoolean(false);
    private final AtomicBoolean isStopped = new AtomicBoolean(false);

    @Autowired
    public IndexingServiceImpl(SiteRepository siteRepository, PageRepository pageRepository, SitesList sitesList) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.sitesList = sitesList;
    }

    @Override
    public Void startIndexing() {
        isIndexing.set(true);
        isStopped.set(false);

        for (Site site : sitesList.getSites()) {
            siteId = siteId + 1;
            siteRepository.deleteAllByUrl(site.getUrl());
            siteRepository.createNewSite(site.getName(), site.getUrl(), "INDEXING");

            WebExecutor webExecutor = new WebExecutor(site.getUrl(), siteId, pageRepository);
            webExecutor.compute();

            if (isStopped.get()) {
                siteRepository.updateStatusFailed(site.getUrl(), "Индексация остановлена пользователем");
                break;
            } else {
                siteRepository.updateStatusIndexed(site.getUrl());
            }
        }

        isIndexing.set(false);
        return null;
    }

    @Override
    public IndexingResponse stopIndexing() {
        if (!isIndexing.get()) {
            // Возвращаем ответ с ошибкой, если индексация не запущена
            return new IndexingResponse(false, "Индексация не запущена");
        }

        isStopped.set(true);
        isIndexing.set(false);

        List<String> indexingSites = siteRepository.findByStatus("INDEXING");
        for (String site : indexingSites) {
            siteRepository.updateStatusFailed(site, "Индексация остановлена пользователем");
        }
        return new IndexingResponse(true, null);
    }

    @Override
    public boolean isIndexing() {
        return isIndexing.get();
    }
}