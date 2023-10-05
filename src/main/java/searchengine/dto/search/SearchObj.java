package searchengine.dto.search;

import lombok.Data;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;

@Data
public class SearchObj {
    private PageModel pageModel;
    private LemmaModel lemmaModel;
    private IndexModel indexModel;
    private Float absolRelev;

}
