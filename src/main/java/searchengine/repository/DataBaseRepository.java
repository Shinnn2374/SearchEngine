package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;

import javax.transaction.Transactional;
import java.time.Instant;


@Repository
public interface DataBaseRepository extends JpaRepository<Site, Integer>{

    // Метод который создает новую запись в таблице Site с статусом 'INDEXING'
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO site (name, status, status_time, last_error, url) " +
            "VALUES (:name,'INDEXING', :statusTime, NULL, :url)", nativeQuery = true)
    void createSiteByUrl(
            @Param("name") String name,
            @Param("url") String url,
            @Param("statusTime") Instant statusTime
    );

    // Метод который удаляет все записи из таблицы Site по заданному url
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM site WHERE url = :url", nativeQuery = true)
    void deleteByUrl(@Param("url") String url);

    // Метод который обновляет статус записи в таблице Site по окончанию индексации
    @Modifying
    @Transactional
    @Query(value = "UPDATE site SET status = 'INDEXED', status_time = :statusTime WHERE status = 'INDEXING'", nativeQuery = true)
    void updateIndexingSitesToIndexed(@Param("statusTime") Instant statusTime);

    // Метод который добавляет данные в таблицу Page
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO page(code, content, path, site_id) VALUES ( :code, :content, :path, :site_id)", nativeQuery = true)
    void insertPage(@Param("code") Integer code,@Param("path") String path,@Param("content") String content, @Param("site_id") Integer siteId);


    // Метод который изменяет статусы при остановки индексации
    @Modifying
    @Transactional
    @Query("UPDATE Site s SET s.status = 'FAILED', s.lastError = ?1 WHERE s.status = 'INDEXING'")
    void updateIndexingSitesToFailed(String errorMessage);
}
