package searchengine.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import searchengine.utils.SiteStatus;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@Entity
@ToString
@Table(name = "site")
public class site
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "site_id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "site_status", nullable = false)
    private SiteStatus status;

    @CreationTimestamp
    @Column(name = "status_time")
    private Instant statusTime;

    @Column(name = "last_error")
    private String lastError;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String name;
}
