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
@Table(name = "Lemmas")
public class Lemma {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id", nullable = false)
    private int id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "site_id", nullable = false, foreignKey = @ForeignKey(name="key_lemma_site"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    @BatchSize(size = 10)
    private Site site;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String lemma;
    @Column(nullable = false)
    private int frequency;
}
