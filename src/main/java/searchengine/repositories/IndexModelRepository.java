package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

import java.util.List;

@Repository
public interface IndexModelRepository extends JpaRepository<IndexModel, Integer> {

  //  List<IndexModel> findByPageModelByPageId(PageModel pageModel);
//
    @Transactional
    List<IndexModel> findAllByLemmaModelId(LemmaModel lemmaModel);
    @Transactional
    List<IndexModel> findAllByLemmaModelIdAndPageModelId(LemmaModel lemmaModel, PageModel pageModel);

    //List<IndexModel> findAllBylemmaModelBylemmaAndPageModelId(String lemma, PageModel pageModel);

   // List<IndexModel> findAllByIndexModelBylemma(String lemma);
    //@Query("select i from IndexT i join PageT  p on p.pageId = i.pageId where i.lemmaTByLemmaId = ?1 and p.siteTBySiteId = ?2")
//    List<IndexModel> findAllByLemmaModelByLemmaIdAndSiteId(LemmaModel lemmaT, SiteModel siteT);

}
