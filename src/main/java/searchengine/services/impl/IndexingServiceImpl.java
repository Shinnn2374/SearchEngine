package searchengine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;
import searchengine.services.IndexingService;
import searchengine.utils.WebExecutor;


@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService
{
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final SitesList sitesList;
    private Integer siteId = 0;

    @Override
    public Void startIndexing() {
        for (Site site : sitesList.getSites()) {
            siteId = siteId +1;
            siteRepository.deleteAllByUrl(site.getUrl());
            siteRepository.createNewSite(site.getName(),site.getUrl(), "INDEXING");
            WebExecutor webExecutor = new WebExecutor(site.getUrl(), siteId, pageRepository);
            webExecutor.compute();
            siteRepository.updateStatus();
        }
        return null;
    }

    @Override
    public Void stopIndexing() {
        return null;
    }
}
