package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;


import java.util.List;

@Repository
public interface PageModelRepository extends JpaRepository<PageModel, Integer>
{
    @Transactional
    void deleteAllBySiteModelId(SiteModel siteModel);

    int countBySiteModelId(SiteModel siteModel);

    @Transactional
    PageModel findByPathAndSiteModelId(String path, SiteModel siteModel);

//    List<PageModel> findBySiteModelBySiteIdAndCode(SiteModel site, int code);
//
//    int countBySiteModelBySiteId(SiteModel siteModel);
//
    // PageModel findBysiteModelId(int pageId);
//
//    PageModel findBySiteTBySiteIdAndPath(SiteModel siteModel, String path);
}
