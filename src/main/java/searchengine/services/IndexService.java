package searchengine.services;

import searchengine.dto.index.IndexResponse;

public interface IndexService
{
    IndexResponse startIndexing();
    IndexResponse stopIndexing();
}
