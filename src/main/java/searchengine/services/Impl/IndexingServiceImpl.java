package searchengine.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.repository.DataBaseRepository;
import searchengine.services.IndexingService;


@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    private final DataBaseRepository repository;
    private final SitesList sitesList;

    @Override
    public Void startIndexing() {
        return null;
    }

    @Override
    public Void stopIndexing() {
        return null;
    }
}
