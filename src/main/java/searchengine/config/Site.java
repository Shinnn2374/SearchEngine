package searchengine.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс для сущности site
 */

@Setter
@Getter
public class Site {
    /**
     * Поле для передачи адреса сайта
     */
    private String url;
    /**
     * Поле для передачи названия сайта
     */
    private String name;
}
