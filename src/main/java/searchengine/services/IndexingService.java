package searchengine.services;


import searchengine.dto.indexing.IndexingResponse;

public interface IndexingService
{
    Void startIndexing();
    boolean isIndexing();
    IndexingResponse stopIndexing();
}
