package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "index")
@Entity
public class IndexModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "index_id", nullable = false)
    private Integer indexId;
    @ManyToOne(targetEntity = PageModel.class)
    @JoinColumn(name = "page_id", nullable = false, insertable = false, updatable = false)
    private PageModel pageModelId;
    @ManyToOne(targetEntity = LemmaModel.class)
    @JoinColumn(name = "lemma_id", nullable = false) // insertable = false, updatable = false)
    private LemmaModel lemmaModelId;
    @Column(name = "ranks")
    private float rank;
//    @ManyToOne
//    @JoinColumn(name = "lemma_id", referencedColumnName = "lemma_id", nullable = false, insertable = false, updatable = false)
//    private LemmaModel lemmaModelByLemmaId;
}
