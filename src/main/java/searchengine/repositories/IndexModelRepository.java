package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.dto.search.Page;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

import java.util.List;

@Repository
public interface IndexModelRepository extends JpaRepository<IndexModel, Integer> {

  //  List<IndexModel> findByPageModelByPageId(PageModel pageModel);
//


  @Query(value = """
            SELECT new searchengine.services.Page(idx.pageId, CAST(SUM(idx.rank) as double)) \
            FROM Index idx \
            WHERE idx.pageId IN :pageIds AND idx.lemmaId IN :lemmaIds GROUP BY idx.pageId""")
  List<Page> getRankSumByPageIdsAndLemmaIds(List<Integer> pageIds, List<Integer> lemmaIds);

    @Transactional
    List<IndexModel> findAllByLemmaModelId(LemmaModel lemmaModel);
    @Transactional
    List<IndexModel> findAllByLemmaModelIdAndPageModelId(LemmaModel lemmaModel, PageModel pageModel);
    @Query(value = "SELECT idx.pageId FROM Index idx WHERE idx.lemmaId = :lemmaId")
    List<Integer> findPageIdsByLemmaId(int lemmaId);

    //List<IndexModel> findAllBylemmaModelBylemmaAndPageModelId(String lemma, PageModel pageModel);

   // List<IndexModel> findAllByIndexModelBylemma(String lemma);
    //@Query("select i from IndexT i join PageT  p on p.pageId = i.pageId where i.lemmaTByLemmaId = ?1 and p.siteTBySiteId = ?2")
//    List<IndexModel> findAllByLemmaModelByLemmaIdAndSiteId(LemmaModel lemmaT, SiteModel siteT);

}
