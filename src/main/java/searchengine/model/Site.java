package searchengine.model;

import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Sites")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(columnDefinition = "enum", nullable = false)
    private StatusOption status;
    @Column(name = "status_time", nullable = false)
    private LocalDateTime statusTime;
    @Column(name = "last_error")
    private String lastError;
    @NonNull
    private String url;
    @NonNull
    private String name;
}
