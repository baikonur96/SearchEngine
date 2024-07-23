package searchengine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Pages")
public class PageModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(targetEntity = SiteModel.class)
    @JoinColumn(name = "site_id" /*,nullable = false, insertable = false, updatable = false*/)
    private SiteModel siteModelId;
    @Column(columnDefinition = "TEXT NOT NULL, Index (path(128))")
    private String path;
    @Column(nullable = false)
    private int code;
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;
//    @ManyToOne
//    @JoinColumn(name = "site_id", referencedColumnName = "site_id", nullable = false, insertable = false, updatable = false)
//    private SiteModel siteModelBySiteId;

}
