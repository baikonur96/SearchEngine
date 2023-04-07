package searchengine.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.persistence.*;

@Entity
@Table(name = "Pages")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
  //  @JoinColumn(name = "site_id", nullable = false)
   // @Column(name = "site_id")
   // @ManyToOne(cascade = CascadeType.ALL)
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "site_id", nullable = false)
    private Site siteId;
    @NonNull
    private String path;
    @NonNull
    @Column(nullable = false)
    private String code;
    @Column(nullable = false)
    private String content;

}
