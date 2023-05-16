package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import searchengine.model.Page;

public interface PageRepository extends CrudRepository<Page, Integer> //JpaRepository<Page, Integer>
{
}
