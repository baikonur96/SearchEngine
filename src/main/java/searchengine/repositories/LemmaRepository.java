package searchengine.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import searchengine.model.Lemma;
import searchengine.model.Page;

public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
}
