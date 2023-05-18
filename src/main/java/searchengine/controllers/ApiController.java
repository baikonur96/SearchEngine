package searchengine.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.StatisticsService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

@RestController
@RequestMapping("/api")
public class ApiController {

    public final String START_URL = "https://skillbox.ru/";

    private final StatisticsService statisticsService;

    public ApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<Map<String, Boolean>> StartIndex() {
        HashMap<String, Boolean> response = new HashMap<>();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();



            StringBuffer result  = new StringBuffer();

            ForkJoinPool pool = new ForkJoinPool();
            result.append(pool.invoke(new Sta))
        //    result.append(pool.invoke(new services.StartIndexingService(START_URL, 0)));



            return ResponseEntity.ok(response);
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Boolean> AddUpdateIndex(@RequestParam String url) {
        Boolean response = true;
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        System.out.println(url);

        return ResponseEntity.ok(response);
    }

}
