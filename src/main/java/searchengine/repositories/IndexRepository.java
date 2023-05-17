package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import searchengine.model.Index;
import searchengine.model.Page;

public interface IndexRepository extends JpaRepository<Index, Integer> {
}
