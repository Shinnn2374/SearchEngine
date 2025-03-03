package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 *  Класс для создания таблицы Index в БД MySQL
 */

@Getter
@Setter
@Entity
@Table(name = "search_index")
public class Index {

    /**
     * Primary key, уникальный идентификатор индекса в таблице
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    /**
     * Поле в котором хранится ID проиндексированной страницы
     */
    @Column(name = "page_id", nullable = false)
    private Integer pageId;
    /**
     * Поле в котором хранится ID леммы
     */
    @Column(name = "lemma_id", nullable = false)
    private Integer lemmaId;
    /**
     * Поле в котором хранится количество леммы для данной таблицы
     */
    @Column(name = "lemma_rank", nullable = false)
    private Float rank;
}