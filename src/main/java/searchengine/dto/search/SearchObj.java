package searchengine.dto.search;

import lombok.Data;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

import java.util.List;

@Data
public class SearchObj {
    private PageModel pageModel;
   // private LemmaModel lemmaModel;
    private List<IndexModel> listIndexModel;
    private Float absRel;
    private Float relRel;

}
