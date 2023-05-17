package searchengine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "indexs")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne(targetEntity = Page.class)
    @JoinColumn(name = "page_id", nullable = false, insertable = false, updatable = false)
    private Page pageId;
    @ManyToOne(targetEntity = Lemma.class)
    @JoinColumn(name = "lemma_id", nullable = false, insertable = false, updatable = false)
    private Lemma lemmaId;
    @Column(name = "ranks")
    private float rank;
}
