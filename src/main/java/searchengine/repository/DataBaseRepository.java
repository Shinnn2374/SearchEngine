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

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Site (status, status_time, last_error, url, name) " +
            "VALUES ('INDEXING', :statusTime, NULL, :url, :url)", nativeQuery = true)
    void createSiteByUrl(
            @Param("url") String url,
            @Param("statusTime") Instant statusTime
    );


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM site WHERE url = :url", nativeQuery = true)
    void deleteByUrl(@Param("url") String url);

    @Modifying
    @Transactional
    @Query(value = "UPDATE site SET status = 'INDEXED', status_time = :statusTime WHERE status = 'INDEXING'", nativeQuery = true)
    void updateIndexingSitesToIndexed(@Param("statusTime") Instant statusTime);
}
