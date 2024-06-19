package searchengine.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;


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
    @CreationTimestamp
    @Column(name = "status_time", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime statusTime;
    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;
    @Column(name = "url", nullable = false, columnDefinition = "VARCHAR(255)")
    private String url;
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @OneToMany(mappedBy = "siteTBySiteId", cascade = CascadeType.MERGE)
//    private Collection<LemmaModel> lemmaModelBySiteId;


}
