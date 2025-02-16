package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;

@Repository
public interface PageRepository extends JpaRepository<Page, Integer> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO Page (path, code, content) VALUES ( :path, :code, :content ) ", nativeQuery = true)
    void insertPage(@Param("path") String path, @Param("code") Integer code, @Param("content") String content);
}
