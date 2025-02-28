package searchengine.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.repository.DataBaseRepository;
import searchengine.services.IndexingService;
import searchengine.utils.LinkExecutor;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    private final DataBaseRepository repository;
    private final SitesList sitesList;
    private static Integer index = 0;

    @Override
    public Void startIndexing() {
        for (Site site : sitesList.getSites()) {
            LinkExecutor linkExecutor = new LinkExecutor(site.getUrl(), index, repository);
            repository.deleteByUrl(site.getUrl());
            repository.createSiteByUrl(site.getUrl(), Instant.now());
            linkExecutor.compute();
            repository.updateIndexingSitesToIndexed(Instant.now());
            index+= 1;
        }
        return null;
    }

    @Override
    public Void stopIndexing() {
        return null;
    }
}
