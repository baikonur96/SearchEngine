package searchengine.controllers;

import lombok.RequiredArgsConstructor;
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

<<<<<<< HEAD
=======
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@RequiredArgsConstructor
>>>>>>> 8e443329ef01fdcba4a4ff7ec9d9bfdc611c6910
@RestController
@RequestMapping("/api")
public class ApiController {
    private static final String DEFAULT_OFFSET = "0";
    private static final String DEFAULT_LIMIT = "20";

    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchService searchService;

//    public ApiController(StatisticsService statisticsService, IndexingService indexingService, SearchService searchService) {
//        this.statisticsService = statisticsService;
//        this.indexingService = indexingService;
//        this.searchService = searchService;
//    }

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
            @RequestParam(defaultValue = DEFAULT_OFFSET, required = false) Integer offset,
            @RequestParam(defaultValue = DEFAULT_LIMIT, required = false) Integer limit) {
//        SearchQueryBuilder sb = SearchQueryBuilder.newBuilder()
//                .withQuery(query)
//                .withSite(site)
//                .withOffset(offset)
//                .withLimit(limit)
//                .build();
        return ResponseEntity.ok(searchService.search(query, site, offset, limit));
     //   return ResponseEntity.ok(searchService.search(sb.getQuery(), sb.getSite(), sb.getOffset(), sb.getLimit()));
    }

}
