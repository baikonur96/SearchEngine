package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "Index")
public class Index {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "page_id", nullable = false, foreignKey = @ForeignKey(name="key_index_page"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @BatchSize(size = 10)
    private Page page;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "lemma_id", nullable = false, foreignKey = @ForeignKey(name="key_index_lemma"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @BatchSize(size = 10)
    private Lemma lemma;
    @Column(name = "rank", columnDefinition = "FLOAT" , nullable = false)
    private float rank;
}
