package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;

import javax.transaction.Transactional;
import java.time.Instant;


@Repository
public interface DataBaseRepository extends JpaRepository<Site, Integer> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO site(status, status_time, last_error, url, name) " +
            "VALUES (status = 'INDEXING', status_time = :instant , last_error, url, name) ", nativeQuery = true)
    void createWithStatusIndexing(Instant instant);
}
