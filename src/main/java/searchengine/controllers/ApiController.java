package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;
import searchengine.services.StatisticsService;

/**
 * Класс контроллера для взаимодействия с сайтом
 */

@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * Поле для создания сущности - сервис статистики
     */
    private final StatisticsService statisticsService;
    /**
     * Поле для создания сущности - сервиса индексации
     */
    private final IndexingService indexingService;

    public ApiController(StatisticsService statisticsService, IndexingService indexingService) {
        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
    }

    /**
     * Метод для начала сбора статистики по индексированным сайтам
     * @return Возвращает так называемый ответ
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    /**
     * Метод для начала индексации
     * @return Ничего не возвращает
     */
    @GetMapping("/startIndexing")
    public ResponseEntity<Void> startIndexing() {
        return ResponseEntity.ok(indexingService.startIndexing());
    }

    /**
     * Метод для остановки индексации
     * @return Ничего не возвращает
     */
    @GetMapping("/stopIndexing")
    public ResponseEntity<Void> stopIndexing() {
        return ResponseEntity.ok(indexingService.stopIndexing());
    }
}
