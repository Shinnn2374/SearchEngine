package searchengine.services;

/**
 * Интерфейс для структуризации сервисов, интерфейс отвечающий за индексацию
 */
public interface IndexingService {
    /**
     * Метод запускающий индексацию сайта
     * @return null
     */
    Void startIndexing();

    /**
     * Метод для остановки индексации сайтов
     * @return null
     */
    Void stopIndexing();
}
