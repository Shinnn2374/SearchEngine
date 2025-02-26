package searchengine.services.Impl;

import org.springframework.stereotype.Service;
import searchengine.services.IndexingService;

@Service
public class IndexingServiceImpl implements IndexingService {

    @Override
    public Void startIndexing() {
        return null;
    }

    @Override
    public Void stopIndexing() {
        return null;
    }
}
