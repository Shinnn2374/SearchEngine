package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.site;
import searchengine.utils.SiteStatus;

public interface IndexRepository extends JpaRepository<site,Long>
{
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM site WHERE url = :url", nativeQuery = true)
    void deleteWebsiteDataByUrl(@Param("url") String url);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO site (name, url, site_status) VALUES (:name, :url, :siteStatus)", nativeQuery = true)
    void createNewSiteRecord(@Param("name") String name, @Param("url") String url, @Param("siteStatus") SiteStatus siteStatus);
}
