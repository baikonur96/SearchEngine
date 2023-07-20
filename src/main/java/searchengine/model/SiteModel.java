package searchengine.model;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "Sites")
@AllArgsConstructor
@RequiredArgsConstructor
public class SiteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum('INDEXING', 'INDEXED', 'FAILED')", nullable = false)
    private StatusOption status;
    @Basic
    @Column(name = "status_time", columnDefinition = "DATETIME")
    private Timestamp statusTime;
    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;
    @Column(name = "url", nullable = false, columnDefinition = "VARCHAR(255)")
    private String url;
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;


}
