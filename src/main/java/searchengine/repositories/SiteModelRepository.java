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
    @Transactional
    Optional<List<SiteModel>> findByName(String name);
    //Optional<List<SiteModel>> findByUrl(String url);

    @Transactional
    boolean existsByUrl(String url);

    @Transactional
    List<SiteModel> findAll();

    @Transactional
    void deleteAllByName(String name);
    @Transactional
    void deleteAllByUrl(String url);
    @Transactional
    int countByNameAndStatus(String name, StatusOption status);
    @Transactional
    SiteModel findByUrl(String url);
    @Transactional
    List<SiteModel> findByStatus(StatusOption status);


//    @Query(value = "select s from SiteModel s join PageModel p on s.id = p.id where p.id =?1")
//    @Transactional
//    SiteModel findByPageId(int page);

}
