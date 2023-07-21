package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.jsoup.nodes.Document;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;
import searchengine.repositories.Utils;

import java.io.IOException;
import java.lang.invoke.WrongMethodTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;


@Service
@RequiredArgsConstructor
public class SiteParse implements Runnable {

    private static ConcurrentLinkedQueue<ForkJoinPool> poolList = new ConcurrentLinkedQueue<>();
    private ForkJoinPool pool;
    private String domain;
    private String url;
    private int parallelism;
    private SiteModel siteModel;
    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;
//    private final LemmaModelRepository lemmaTRepository;
//    private final IndexModelRepository indexTRepository;
//    private final LemmaParser lemmaParser;


  //  private PageModelRepository pageModelRepository;

//    public SiteParse(String url, int level) {
//        //this();
//        this.url = url;
//        this.level = level;
//    }


    public SiteParse copy(){
        return new SiteParse(this.pageModelRepository, this.siteModelRepository);
    }

    public void init(SiteModel siteModel, int parallelism) {
        this.url = siteModel.getUrl();
        this.domain = Utils.getProtocolAndDomain(url);
        this.parallelism = parallelism;
        this.siteModel = siteModel;
    }




    @Override
    public void run() {

        PageParse pageParse = new PageParse(url, pageModelRepository.findBySiteTBySiteIdAndPath(siteModel, url.toString()));
    }
}
