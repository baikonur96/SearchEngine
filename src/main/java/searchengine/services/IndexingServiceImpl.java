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
import searchengine.repositories.*;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;
    private final LemmaModelRepository lemmaModelRepository;
    private final IndexModelRepository indexModelRepository;
    private final LemmaParse lemmaParse;
    private final List<SiteModel> siteModelList = new Vector<>();
    private final SitesList sites;
    private final SiteParse siteP;


    public String UpdateUrl(String url){
//        if (url.endsWith("/")){
//            return new String(url.substring(0, url.length() - 1));
//        }
        if (url.contains("www."))
        {
            return new String(url.substring(0, 8) + url.substring(12));
        }
        return url;
    }


    @Override
    @Transactional
    public IndexingResponse getStartIndexing() {
        siteModelList.clear();
        IndexingResponse response = new IndexingResponse();
        try {
            // response = new IndexingResponse();
            List<Site> siteList = sites.getSites();
            if (siteList.stream()
                    .map(e -> siteModelRepository.countByNameAndStatus(e.getName(), StatusOption.INDEXING))
                    .reduce(0, Integer::sum) > 0) {
                response.setResult(false);
                response.setError("Индексация уже запущена getStartIndexing");
            } else {
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
                executor.setMaximumPoolSize(Runtime.getRuntime().availableProcessors());
                for (Site site : siteList) {
                    String name = site.getName();
                    Optional<List<SiteModel>> byName = siteModelRepository.findByName(name);
                    if (byName.isPresent()) {
                        SiteModel siteModel = siteModelRepository.findByUrl(UpdateUrl(site.getUrl().trim()));
                        pageModelRepository.deleteAllBySiteModelId(siteModel);
                        siteModelRepository.deleteAllByName(name);
                    }
                    SiteModel siteModel = new SiteModel();
                    siteModel.setStatus(StatusOption.INDEXING);
                    siteModel.setStatusTime(Utils.getTimeStamp().toLocalDateTime());
                    siteModel.setUrl(UpdateUrl(site.getUrl()));
                    siteModel.setName(site.getName());
                    siteModelRepository.save(siteModel);
                    siteModelList.add(siteModel);
                    System.out.println("Отдал в поток " + siteModel.getName());
                    SiteParse siteParse = siteP.copy();
                   // SiteParse siteParse = new SiteParse(pageModelRepository, siteModelRepository);
                    siteParse.setSiteId(siteModel.getId());
                    siteParse.setSiteUrl(siteModel.getUrl());
                    executor.submit(siteParse);
                }

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
        try {
            long size = siteModelList.stream().filter(e -> e.getStatus() == StatusOption.INDEXING).count();
            if (size == 0) {
                response.setResult(false);
                response.setError("Индексация не запущена");
            } else {
                System.out.println(siteModelList.size());
                System.out.println(siteModelList.get(0).getStatus());
                System.out.println(siteModelList.get(1).getStatus());
                System.out.println(siteModelList.get(2).getStatus());
                siteModelList.stream()
                      //  .filter(e -> e.getStatus() == StatusOption.INDEXING)
                        .forEach(e -> {
                            e.setStatus(StatusOption.FAILED);
                            e.setStatusTime(Utils.getTimeStamp().toLocalDateTime());
                            e.setLastError("Индексация остановлена пользователем");
                        });
                siteModelRepository.saveAll(siteModelList);
                SiteParse.forceStop();
                response.setResult(true);
            }
        } catch (Exception e) {
            response.setResult(false);
            response.setError(e.getMessage());
        }
        return response;
    }

    @Override
    public IndexingResponse indexPage(String url) {
        IndexingResponse response = new IndexingResponse();
        List<SiteModel> listSiteModel = siteModelRepository.findAll();
        for (SiteModel siteModelExp : listSiteModel){
                    if (url.contains(siteModelExp.getUrl())){
                        pageModelRepository.deleteAllBySiteModelId(siteModelExp);
                        siteModelRepository.deleteAllByUrl(siteModelExp.getUrl());
                        PageParse pageParse = new PageParse(pageModelRepository,
                                siteModelRepository,
                                lemmaModelRepository,
                                indexModelRepository,
                                lemmaParse);

                        pageParse.setSiteId(siteModelExp.getId());
                        pageParse.setSiteUrl(siteModelExp.getUrl());
                        pageParse.setPage(url);

                        response.setResult(true);
                        return response;
                    }
        }
        response.setResult(false);
        response.setError("Данная страница находится за пределами сайтов,\n" +
                "указанных в конфигурационном файле");
        return response;
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

