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

    @Override
    public Void startIndexing() {
        for (Site site : sitesList.getSites()) {
            siteRepository.deleteAllByUrl(site.getUrl());
            siteRepository.createNewSite(site.getName(),site.getUrl(), "INDEXING");
            WebExecutor webExecutor = new WebExecutor(site.getUrl());
            webExecutor.compute();
        }
        return null;
    }

    @Override
    public Void stopIndexing() {
        return null;
    }
}
