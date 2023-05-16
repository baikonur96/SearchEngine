package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import searchengine.model.Site;

public interface SiteRepository extends CrudRepository<Site, Integer>   //JpaRepository<Site, Integer>
{

}
