package searchengine.repositories;

import org.apache.catalina.startup.ContextRuleSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;

import java.util.List;

@Repository
public interface LemmaModelRepository extends JpaRepository<LemmaModel, Integer> {

//    List<LemmaModel> findAllByLemmaId(int lemmaId);
//
   // List<LemmaModel> findAllByLemma(String lemma);

  //  LemmaModel findByLemmaAndSiteModelBySiteId();

    @Transactional
    void deleteAllBySiteModelId(SiteModel siteModel);

    @Transactional
    boolean existsByLemma(String lemma);

    @Transactional
    boolean existsByLemmaAndSiteModelId(String lemma, SiteModel siteModel);

    @Transactional
    List<LemmaModel> findAllByLemma(String lemma);

    @Transactional
    LemmaModel findByLemmaAndSiteModelId(String lemma, SiteModel siteModel);

    @Transactional
    int countBySiteModelId(SiteModel siteModel);
//    @Transactional
//    LemmaModel findByLemmaAndSiteModel(String lemma, SiteModel siteModel);

//    @Transactional
//    void deleteBySiteModelBySiteIdAndFrequency(SiteModel siteModel, int frequency);
//
//    int countBySiteModelBySiteId(SiteModel siteT);

}
