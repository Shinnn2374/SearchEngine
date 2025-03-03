package searchengine.services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.repository.DataBaseRepository;
import searchengine.services.IndexingService;
import searchengine.utils.LinkExecutor;

import java.time.Instant;

/**
 * Сервис для работы с индексацией сайтов
 * Представленные методы служат для начала индексации и ее остановки
 * @author Shinnn2374
 * @since 2025-03-03
 * @see DataBaseRepository
 * @see LinkExecutor
 * @see SitesList
 */

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    /**
     * Поле для создания объекта репозитория
     */
    private final DataBaseRepository repository;
    /**
     * Поле для создания объекта - списка сайтов
     */
    private final SitesList sitesList;

    /**
     * Поле для создания счетчика, для корректного добавления страниц в таблицу Page
     */
    private static Integer siteId = 1;

    /**
     * Метод для начала индексации сайтов из списка SiteList
     * @see SitesList
     * @see LinkExecutor
     * Не получает никаких параметров
     * @return не возвращает никакое значение
     */
    @Override
    public Void startIndexing() {
        for (Site site : sitesList.getSites()) {
            LinkExecutor linkExecutor = new LinkExecutor(site.getUrl(), siteId, repository);
            repository.deleteByUrl(site.getUrl());
            repository.createSiteByUrl(site.getName() ,site.getUrl(), Instant.now());
            linkExecutor.compute();
            repository.updateIndexingSitesToIndexed(Instant.now());
            siteId += 1;
        }
        return null;
    }

    /**
     * Метод для остановки индексации сайтов из списка SiteList
     * @see SitesList
     * @see LinkExecutor
     * Не получает никаких параметров
     * @return не возвращает никакое значение
     */
    @Override
    public Void stopIndexing() {
        LinkExecutor.stop();
        repository.updateIndexingSitesToFailed("Индексация остановлена пользователем");
        return null;
    }
}
