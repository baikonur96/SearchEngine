package searchengine.services;


import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.model.SiteModel;
import searchengine.model.StatusOption;
import searchengine.repositories.SiteModelRepository;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    @Autowired
    private final SiteModelRepository siteModelRepository;

    private final SitesList sites;


    @Override
    @Transactional
    public IndexingResponse getStartIndexing() {
        IndexingResponse response = new IndexingResponse();
        List<Site> siteList = sites.getSites();
        if (siteList.stream()
                .map(e -> siteModelRepository.countByNameAndStatus(e.getName(), StatusOption.INDEXING))
                .reduce(0, Integer::sum) > 0) {
            response.setResult(false);
            response.setError("Индексация уже запущена");
        } else {

                response.setError("TRUE");
//            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
//            executor.setMaximumPoolSize(Runtime.getRuntime().availableProcessors());
//            siteList.forEach(e -> {
//                SiteParse sp = siteParse.copy();
//                String name = e.getName();
//                Optional<List<SiteModel>> byName = siteModelRepository.findByName(name);
//                if (byName.isPresent()) {
//                    siteModelRepository.deleteAllByName(name);
//                }
//                SiteModel siteModel = new SiteModel(StatusOption.INDEXING, Utils.getTimeStamp(), e.getUrl(), e.getName());
//                siteModelRepository.save(siteModel);
//                siteTList.add(siteT);
//                sp.init(siteT, 3);
//                executor.execute(sp);
//
//            });
            response.setResult(true);
        }
        return response;
    }

    @Override
    public IndexingResponse getStopIndexing() {
        return null;
    }

    @Override
    public IndexingResponse indexPage(String url) {
        return null;
    }
}

