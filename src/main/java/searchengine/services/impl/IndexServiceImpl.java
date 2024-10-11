package searchengine.services.impl;

import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.index.IndexResponse;
import searchengine.repository.IndexRepository;
import searchengine.services.IndexService;
import searchengine.utils.SiteStatus;

@Service
public class IndexServiceImpl implements IndexService
{
    private IndexRepository indexRepository;
    private SitesList sitesList;

    @Override
    public IndexResponse startIndexing()
    {
        IndexResponse indexResponse = new IndexResponse();
        for (Site newSite : sitesList.getSites())
        {
            indexRepository.deleteWebsiteDataByUrl(newSite.getUrl());
            indexRepository.createNewSiteRecord(newSite.getName(),newSite.getUrl(), SiteStatus.INDEXING);

        }
        indexResponse.setResult(true);
        return indexResponse;
    }
}
