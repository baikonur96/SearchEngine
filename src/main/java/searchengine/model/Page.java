package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import javax.persistence.Index;

@Getter
@Setter
@Entity
@Table(name = "Pages", indexes = {@Index(name = "path_index", columnList = "path", unique = false)})
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "site_id", nullable = false, foreignKey = @ForeignKey(name="k_page_site"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @BatchSize(size = 10)
    private Site site;
    @Column(columnDefinition = "TEXT NOT NULL, Index (path(128))", name = "path")
    private String path;
    @Column(nullable = false)
    private int code;
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

}
