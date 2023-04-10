package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import searchengine.model.StatusOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Sites")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    private StatusOption status;
    @Column(name = "status_time", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime statusTime;
    @Column(name = "last_error", nullable = false, columnDefinition = "TEXT")
    private String lastError;
    @Column(name = "url", nullable = false, columnDefinition = "VARCHAR(255)")
    private String url;
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;


}
