package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;

import java.util.List;

@Repository
public interface IndexModelRepository extends JpaRepository<IndexModel, Integer> {

    List<IndexModel> findByPageModelByPageId(PageModel pageModel);
//
    List<IndexModel> findAllByLemmaModelByLemmaId(LemmaModel lemmaModel);

    List<IndexModel> findAllBylemmaModelBylemmaAndPageModelId(String lemma, PageModel pageModel);

    List<IndexModel> findAllByLemmaModelBylemma(String lemma);
    //@Query("select i from IndexT i join PageT  p on p.pageId = i.pageId where i.lemmaTByLemmaId = ?1 and p.siteTBySiteId = ?2")
//    List<IndexModel> findAllByLemmaModelByLemmaIdAndSiteId(LemmaModel lemmaT, SiteModel siteT);

}
