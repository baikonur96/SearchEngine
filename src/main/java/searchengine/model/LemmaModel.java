package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.SQLInsert;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "lemmas",
uniqueConstraints = {
        @UniqueConstraint(name = "uniqueKey", columnNames = {"site_id", "lemma"})
})
@SQLInsert(sql = "insert into lemmas(site_id, lemma, frequency) values (?, ?, ?) on duplicate key update frequency = lemmas.frequency + 1")
public class LemmaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id"/*, nullable = false*/)
    private Integer id;
    @ManyToOne(targetEntity = SiteModel.class)
    @JoinColumn(name = "site_id"/*, nullable = false*/)
    private SiteModel siteModelId;
    @Column(columnDefinition = "VARCHAR(255)"/*, nullable = false*/)
    private String lemma;
    @Column(nullable = false)
    private int frequency;

//    public LemmaModel(SiteModel siteModelId, String lemma, int frequency) {
//        this.frequency = frequency;
//        this.siteModelId = siteModelId;
//        this.lemma = lemma;
//    }
}
