package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteModel;

import org.springframework.transaction.annotation.Transactional;
import searchengine.model.StatusOption;

import java.util.List;
import java.util.Optional;

@Repository
public interface SiteModelRepository extends JpaRepository<SiteModel, Integer>
{

    Optional<List<SiteModel>> findByName(String name);

    @Transactional
    void deleteAllByName(String name);

    int countByNameAndStatus(String name, StatusOption status);

    SiteModel findByUrl(String url);

    @Query(value = "select s from SiteModel s join PageModel p on s.id = p.id where p.id =?1")
    SiteModel findByPageId(int page);

}
