package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchResponse;
import searchengine.repositories.IndexModelRepository;
import searchengine.repositories.LemmaModelRepository;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;


@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;
    private final LemmaModelRepository lemmaModelRepository;
    private final IndexModelRepository indexModelRepository;



    @Override
    public SearchResponse search(String query, String site, Integer offset, Integer limit) {





        return null;
    }
}
