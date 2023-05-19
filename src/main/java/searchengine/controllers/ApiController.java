package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IndexingService;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping("/api")
public class ApiController {


    private final StatisticsService statisticsService;

    private final IndexingService indexingService;

   // private final SearchService searchService;

    public ApiController(StatisticsService statisticsService, IndexingService indexingService) {
        this.statisticsService = statisticsService;
        this.indexingService = indexingService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<IndexingResponse> StartIndex() {
       // String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
          //  StringBuffer result  = new StringBuffer();

          //  ForkJoinPool pool = new ForkJoinPool();
        //    result.append(pool.invoke(new IndexingService(START_URL, 0)));
        //    result.append(pool.invoke(new services.StartIndexingService(START_URL, 0)));
            return ResponseEntity.ok(indexingService.getStartIndexing());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Boolean> AddUpdateIndex(@RequestParam String url) {
        Boolean response = true;
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        System.out.println(url);

        return ResponseEntity.ok(response);
    }

}
