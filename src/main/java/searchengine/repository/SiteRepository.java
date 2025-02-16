package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;
import searchengine.utils.EnumStatus;

import javax.transaction.Transactional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Integer> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Site  WHERE url = :url", nativeQuery = true)
    void deleteAllByUrl(@Param("url") String url);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Site (status, status_time, last_error, url, name) VALUES (:status, current_timestamp(), NULL , :url, :name)", nativeQuery = true)
    void createNewSite(@Param("name") String name, @Param("url") String url, @Param("status") String status);
}
