package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import searchengine.utils.SiteStatus;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

/**
 * Класс для создания таблицы Site в БД MySQL
 */
@Getter
@Setter
@Entity
@Table(name = "site")
public class Site {

    /**
     * Primary key, уникальный идентификатор сайта в таблице
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Поле для отображения состояния сайта во время/после индексации
     * @see SiteStatus
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SiteStatus status;

    /**
     * Поле хранящее время изменения статуса сайта
     */
    @Column(name = "status_time", nullable = false)
    private Instant statusTime;

    /**
     * Поле для хранения ошибки(Если она возникла в процессе индексации)
     */
    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;

    /**
     * Поле для хранения адреса сайта
     */
    @Column(name = "url", nullable = false, length = 255)
    private String url;

    /**
     * Поле для хранения названия сайта
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;
}