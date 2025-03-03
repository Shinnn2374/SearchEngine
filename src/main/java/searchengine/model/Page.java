package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Класс для создания таблицы Page в БД MySQL
 */

@Getter
@Setter
@Entity
@Table(name = "page")
public class Page {

    /**
     * Primary key, уникальный идентификатор страницы в таблице
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     *  ID сайта которому принадлежит данная страница
     */
    @ManyToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    /**
     * Поле для указания пути к странице сайта
     */
    @Column(name = "path", nullable = false, columnDefinition = "TEXT")
    private String path;

    /**
     * Код ответа полученного при попытке открытия страницы
     */
    @Column(name = "code", nullable = false)
    private Integer code;

    /**
     * Поле для хранения контента хранящегося на сайте
     */
    @Column(name = "content", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;
}