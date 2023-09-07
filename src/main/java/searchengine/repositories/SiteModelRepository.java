package searchengine.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
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
    //Optional<List<SiteModel>> findByUrl(String url);


    boolean existsByUrl(String url);

    // @Transactional
    void deleteAllByName(String name);

    void deleteAllByUrl(String url);

    int countByNameAndStatus(String name, StatusOption status);

    SiteModel findByUrl(String url);

    //@Query(value = "select s from SiteModel s join PageModel p on s.id = p.id where p.id =?1")
    //SiteModel findByPageId(int page);

}
