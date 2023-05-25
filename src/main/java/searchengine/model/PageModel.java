package searchengine.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Pages")
public class PageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Integer id;
    @ManyToOne(targetEntity = SiteModel.class)
    @JoinColumn(name = "site_id", nullable = false, insertable = false, updatable = false)
    private SiteModel siteModelId;
    @Column(columnDefinition = "TEXT NOT NULL, Index (path(128))")
    private String path;
    @Column(nullable = false)
    private int code;
    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    private String content;

}
