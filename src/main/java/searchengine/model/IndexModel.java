package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "indexs")
public class IndexModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(targetEntity = PageModel.class)
    @JoinColumn(name = "page_id")//, nullable = false, insertable = false, updatable = false)
    private PageModel pageModelId;
    @ManyToOne(targetEntity = LemmaModel.class)
    @JoinColumn(name = "lemma_id")//, nullable = false, insertable = false, updatable = false)
    private LemmaModel lemmaModelId;
    @Column(name = "ranks")
    private float rank;
}
