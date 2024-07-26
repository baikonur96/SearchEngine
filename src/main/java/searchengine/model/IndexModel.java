package searchengine.model;

import lombok.*;

import javax.persistence.*;
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "index")
public class IndexModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "index_id", nullable = false)
    private int indexId;
    @NonNull
    @Column(name = "page_id", nullable = false)
    private int pageId;
    @Column(name = "lemma_id", nullable = false)
    @NonNull
    private int lemmaId;
    @NonNull
    @Column(name = "rank", nullable = false)
    private float rank;
    @ManyToOne
    @JoinColumn(name = "page_id", referencedColumnName = "page_id", nullable = false, insertable = false, updatable = false)
    private PageModel pageModelByPageId;
    @ManyToOne
    @JoinColumn(name = "lemma_id", referencedColumnName = "lemma_id", nullable = false, insertable = false, updatable = false)
    private LemmaModel lemmaModelByLemmaId;
}
