package searchengine.services;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.Vector;
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
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.model.StatusOption;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;
import searchengine.repositories.Utils;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;
    private final List<SiteModel> siteModelsList = new Vector<>();
    private final SitesList sites;


    public String UpdateUrl(String url){
        if (url.contains("www.")){
            return new String(url.substring(0, 8) + url.substring(12));
        }
        return url;
    }


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
                for (Site site : siteList) {
                    String name = site.getName();
                    Optional<List<SiteModel>> byName = siteModelRepository.findByName(name);
                    if (byName.isPresent()) {
                        siteModelRepository.deleteAllByName(name);
                    }
                    SiteModel siteModel = new SiteModel();
                    siteModel.setStatus(StatusOption.INDEXING);
                    siteModel.setStatusTime(Utils.getTimeStamp());
                    siteModel.setUrl(UpdateUrl(site.getUrl()));
                 //   System.out.println(site.getUrl() + " ---------- " + UpdateUrl(site.getUrl()));
                    siteModel.setName(site.getName());
                    siteModelRepository.save(siteModel);
                    siteModelsList.add(siteModel);
//                    Thread.sleep(5000);
//                    PageModel pageModel = new PageModel();
//                    pageModel.setSiteModelId(siteModel);
//                    pageModel.setPath("https://skillbox.ru/lol/");
//                    pageModel.setCode(200);
//                    pageModel.setContent("Content");
//                    pageModelRepository.save(pageModel);
                  //  siteP.init(siteModel, 0);
                    System.out.println("Отдал в поток " + siteModel.getName());
                    SiteParse siteParse = new SiteParse(pageModelRepository, siteModelRepository);
                    siteParse.setSiteId(siteModel.getId());
                    siteParse.setSiteUrl(siteModel.getUrl());
                  //  siteParse.init(siteModelRepository, pageModelRepository);
                    executor.submit(siteParse);
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

