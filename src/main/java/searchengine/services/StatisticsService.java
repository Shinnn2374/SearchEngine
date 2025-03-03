package searchengine.services;

import searchengine.dto.statistics.StatisticsResponse;

/**
 * Интерфейс для структуризации сервисов, интерфейс отвечающий за сбор статистики
 */
public interface StatisticsService {
    /**
     * Метод для запуска сбора статистики
     * @return null
     */
    StatisticsResponse getStatistics();
}
