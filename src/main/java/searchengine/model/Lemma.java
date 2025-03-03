package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Класс для создания таблицы Lemma в БД MySQL
 */
@Getter
@Setter
@Entity
@Table(name = "lemma")
public class Lemma{
    /**
     * Primary key, уникальный идентификатор леммы в таблице
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    /**
     * Поле в котором хранится ID сайта с страниц которого собрали данную лемму
     */
    @Column(name = "site_id", nullable = false)
    private Integer siteId;
    /**
     * Поле в котором хранится лемма
     */
    @Column(name = "lemma", nullable = false, length = 255)
    private String lemma;
    /**
     * Поле в котором хранится количество страниц, на которых лемма
     * встречается хотя бы один раз
     */
    @Column(name = "frequency", nullable = false)
    private Integer frequency;
}