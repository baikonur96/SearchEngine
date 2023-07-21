package searchengine.services;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadPoolExecutor;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.indexing.IndexingResponse;
import searchengine.model.SiteModel;
import searchengine.model.StatusOption;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;
import searchengine.repositories.Utils;

@Service
@RequiredArgsConstructor

public class IndexingServiceImpl implements IndexingService {

    @Autowired
    private final SiteModelRepository siteModelRepository;
    private final List<SiteModel> siteModelsList = new ArrayList<>();
    private final SitesList sites;
    private final SiteParse siteParse;




    @Override
    @Transactional
    public IndexingResponse getStartIndexing() {
        IndexingResponse response = new IndexingResponse();
        try {
            response = new IndexingResponse();
            List<Site> siteList = sites.getSites();
            if (siteList.stream()
                    .map(e -> siteModelRepository.countByNameAndStatus(e.getName(), StatusOption.INDEXING))
                    .reduce(0, Integer::sum) > 0) {
                response.setResult(false);
                response.setError("Индексация уже запущена");
            } else {
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
                executor.setMaximumPoolSize(Runtime.getRuntime().availableProcessors());


              //  ForkJoinPool pool = new ForkJoinPool();
                for (Site site : siteList) {
                    SiteParse siteP = siteParse.copy();
                    String name = site.getName();
                    Optional<List<SiteModel>> byName = siteModelRepository.findByName(name);
                    if (byName.isPresent()) {
                        siteModelRepository.deleteAllByName(name);
                    }
                  //  System.out.println("-------------------- " + pool.getPoolSize() + " ----------------");
                //    System.out.println("Сайт IndexServiceImpl - " + site.getUrl());
                 //   pool.invoke(new SiteParse(site.getUrl(), 0));
                    // pool.wait();
                    SiteModel siteModel = new SiteModel();
                    siteModel.setStatus(StatusOption.INDEXED);
                    siteModel.setStatusTime(Utils.getTimeStamp());
                    siteModel.setUrl(site.getUrl());
                    siteModel.setName(site.getName());
                    siteModelRepository.save(siteModel);
                    siteModelsList.add(siteModel);
                    siteP.init(siteModel, 0);
                    executor.execute(siteParse);
                }
//            siteList.forEach(e -> {
//
//
//                // SiteParse sp = siteParse.copy();
//                String name = e.getName();
//                Optional<List<SiteModel>> byName = siteModelRepository.findByName(name);
//                if (byName.isPresent()) {
//                    siteModelRepository.deleteAllByName(name);
//                }
//                ForkJoinPool pool = new ForkJoinPool();
//                StringBuffer res = new StringBuffer();
//                res.append(pool.invoke(new SiteParse(e.getUrl(), 0)));
//                WriteFile(res);
//                // pool.invoke(new SiteParse(e.getUrl(), 0));
//                System.out.println("Сайт - " + e.getUrl());
//                SiteModel siteModel = new SiteModel();
//                siteModel.setStatus(StatusOption.INDEXED);
//                siteModel.setStatusTime(Utils.getTimeStamp());
//                siteModel.setUrl(e.getUrl());
//                siteModel.setName(e.getName());
//                siteModelRepository.save(siteModel);
//                siteModelsList.add(siteModel);
//            });
                response.setResult(true);
            }
            System.out.println("Final start Index");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public IndexingResponse getStopIndexing() {
        IndexingResponse response = new IndexingResponse();
//        try {
//            long size = siteTList.stream().filter(e -> e.getStatus() == StatusOption.INDEXING).count();
//            if (size == 0) {
//                response.setResult(false);
//                response.setError("Индексация не запущена");
//            } else {
//                //SiteParse.forceStop();
//                siteTList.stream()
//                        .filter(e -> e.getStatus() == StatusOption.INDEXING)
//                        .forEach(e -> {
//                            e.setStatus(StatusOption.FAILED);
//                            e.setStatusTime(Utils.getTimeStamp().toLocalDateTime());
//                            e.setLastError("Индексация остановлена пользователем");
//                        });
//                siteModelRepository.saveAll(siteTList);
//
//                response.setResult(true);
//            }
//        } catch (Exception e) {
//            response.setResult(false);
//            response.setError(e.getMessage());
//        }
        return response;
    }

    @Override
    public IndexingResponse indexPage(String url) {
        return null;
    }


    public static void WriteFile(StringBuffer buff) {
        String path = "C:/Users/Adminsvu/Documents/FinalProject_1/SearchEngine/src/main/java/searchengine/outFiles/res.txt";
        try {
            PrintWriter writer = new PrintWriter(path);
            writer.write(buff.toString());
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

