package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.search.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;
import searchengine.services.SearchQueryBuilder;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {


    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchService searchService;

    public ApiController(StatisticsService statisticsService, IndexingService indexingService, SearchService searchService) {
        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
        this.searchService = searchService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> StartIndex() {
        return ResponseEntity.ok(indexingService.getStartIndexing());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<IndexingResponse> StopIndex() {

        return ResponseEntity.ok(indexingService.getStopIndexing());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<IndexingResponse> AddUpdateIndex(@RequestParam String url) {
        return ResponseEntity.ok(indexingService.indexPage(url));
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResponse> search(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "site", required = false) String site,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit) {
        SearchQueryBuilder sb = SearchQueryBuilder.newBuilder()
                .withQuery(query)
                .withSite(site)
                .withOffset(offset)
                .withLimit(limit)
                .build();
        return ResponseEntity.ok(searchService.search(sb.getQuery(), sb.getSite(), sb.getOffset(), sb.getLimit()));
    }

}
