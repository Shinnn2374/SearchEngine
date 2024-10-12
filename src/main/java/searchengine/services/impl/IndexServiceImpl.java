package searchengine.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private SitesList sitesList;

    @Override
    public IndexResponse startIndexing()
    {
        IndexResponse indexResponse = new IndexResponse();
        for (Site newSite : sitesList.getSites())
        {
            indexRepository.deleteWebsiteDataByUrl(newSite.getUrl());
            indexRepository.createSite(newSite.getName(), newSite.getUrl());

        }
        indexResponse.setResult(true);
        return indexResponse;
    }

    @Override
    public IndexResponse stopIndexing() {
        return null;
    }
}
