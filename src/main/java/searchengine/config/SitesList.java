package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс для создания сущности - списка сайтов
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "indexing-settings")
public class SitesList {
    /**
     * Поле в которое передается список сайтов из конфигурации
     */
    private List<Site> sites;
}
