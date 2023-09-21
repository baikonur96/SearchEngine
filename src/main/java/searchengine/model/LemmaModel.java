package searchengine.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLInsert;

import javax.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "Lemmas")
@SQLInsert(sql = "insert into lemmas(frequency,lemma, site_id ) values (?, ?, ?) on duplicate key update frequency = lemma.frequency + 1")
public class LemmaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    @ManyToOne(targetEntity = SiteModel.class)
    @JoinColumn(name = "site_id", referencedColumnName = "site_id", nullable = false, insertable = false, updatable = false)
    private SiteModel siteModelId;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String lemma;
    @Column(nullable = false)
    private int frequency;
}
