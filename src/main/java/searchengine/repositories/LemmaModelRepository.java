package searchengine.repositories;

import org.apache.catalina.startup.ContextRuleSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.LemmaModel;

@Repository
public interface LemmaModelRepository extends JpaRepository<LemmaModel, Integer> {
}
