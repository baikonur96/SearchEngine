package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.jsoup.nodes.Document;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.*;

import java.io.IOException;
import java.lang.invoke.WrongMethodTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.*;

import static java.util.concurrent.ForkJoinTask.invokeAll;


@Service
@Getter
@Setter
@RequiredArgsConstructor
public class SiteParse implements Runnable {
    private static ConcurrentLinkedQueue<ForkJoinPool> poolList = new ConcurrentLinkedQueue<>();
    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;
    private final LemmaModelRepository lemmaModelRepository;
    private final IndexModelRepository indexModelRepository;
    private final LemmaParse lemmaParse;

    private Connection.Response response;
    Integer siteId;
    String siteUrl;

    public SiteParse copy() {
        return new SiteParse(this.pageModelRepository,
                this.siteModelRepository,
                this.lemmaModelRepository,
                this.indexModelRepository,
                this.lemmaParse);
    }

    public boolean CorrectUrl(String startLink, String link) {
        if (!link.isEmpty() && link.startsWith(startLink) &&
                link.trim().length() > (startLink + "/").length() &&
                !link.equals(startLink) &&
                !link.contains("#") &&
                !link.contains(" ")// &&
                //!link.substring(startLink.length(), link.length()).contains(".")
        ) {
            return true;
        }
        return false;
    }

    public List<String> ParseLink(String link) {
        List<String> outputList = new ArrayList<>();
        try {
           // Thread.sleep(150);
            Document document = null;
            response = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) YandexIndexingMachine")
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .ignoreContentType(false)
                    .execute();
//            if (!response.contentType().startsWith("text/html;")) {
//                throw new WrongMethodTypeException("wrong format");
//            }
            if (response.statusCode() != 200) throw new IOException(String.valueOf(response.statusCode()));
            document = response.parse();


            Elements elements = document.select("a");
            for (Element element : elements) {
                String linkString = new String(element.attr("abs:href"));
              //  System.out.println(linkString);
                if (CorrectUrl(link, linkString)) {
                    outputList.add(linkString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputList;
    }

    @Override
    public void run() {
        ForkJoinPool pool = new ForkJoinPool();
        poolList.add(pool);
        List<String> pages = ParseLink(siteUrl);
        for (String page : pages) {
            PageParse pageParse = new PageParse(pageModelRepository,
                    siteModelRepository,
                    lemmaModelRepository,
                    indexModelRepository,
                    lemmaParse);
            pageParse.setSiteId(siteId);
            pageParse.setSiteUrl(siteUrl);
            pageParse.setPage(page);
            //LemmaFinder lemmaFinder = new LemmaFinder();
            pool.invoke(pageParse);
        }
    }

    public static void forceStop() {
        if (poolList != null && !poolList.isEmpty()) {
            poolList.forEach(ForkJoinPool::shutdownNow);
            poolList = new ConcurrentLinkedQueue<>();
        }
    }

}

















//    public static StringBuffer resultBuff = new StringBuffer("https://skillbox.ru/");
//    //private final SiteModel siteModel;
//    public static List<String> linkSet = new Vector<>();
//    private Connection.Response response = null;
//
//    String url;
//    int level;
//
//
//    public boolean CorrectUrl(String startLink, String link) {
//        if (!link.isEmpty() &&
//                link.startsWith(startLink) &&
//                link.length() > startLink.length() &&
//                !link.contains("#") &&
//                !link.contains(" ") &&
//                !link.substring(startLink.length()).contains(".")
//        ) {
//            return true;
//        }
//        return false;
//    }
//    public List<String> ParseLink(String link) {
//        System.out.println("ParseLink: " + link);
//        List<String> outputList = new ArrayList<>();
//        try {
//            Thread.sleep(150);
//            Document document = null;
//                    response = Jsoup.connect(link)
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) YandexIndexingMachine")
//                    .referrer("http://www.google.com")
//                    .timeout(5000)
//                    .ignoreContentType(true)
//                    .execute();
//            if (!response.contentType().startsWith("text/html;")) {
//                throw new WrongMethodTypeException("wrong format");
//            }
//            if (response.statusCode() != 200) throw new IOException(String.valueOf(response.statusCode()));
//            document = response.parse();
//            savePage(document.body().text(), document.title());
//
//
//
//
//
//            Elements elements = document.select("a");
//
//            for (Element ele : elements) {
//               // System.out.println(ele);
//               // System.out.println("ParseLink + element: " + ele);
//                String linkString = new String(ele.attr("abs:href"));
//
//                System.out.println(linkString);
//                if (CorrectUrl(link, linkString) && !linkString.equals(link) && !linkSet.contains(linkString)) {
//                  //  System.out.println("!!!!OK!!!!!! - " + linkString);
//                    linkSet.add(linkString);
//                    outputList.add(linkString);
//                 //   System.out.println("Размер листа: " + outputList.size());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return outputList;
//    }
//
//    public PageModel savePage(String text, String title) {
//
//        int code;
//        if (response == null || (text.equals(""))) {
//            code = 408;
//        } else {
//            code = response.statusCode();
//        }
//        PageModel p = new PageModel(siteModel.getSiteId(), url, code, text, title);
//        pageModelRepository.save(p);
//        siteModel.setStatusTime(Utils.getTimeStamp());
//        siteModelRepository.save(siteModel);
//        return p;
//    }
//
//    @Override
//    protected void compute() {
//        List<SiteParse> listTask = new ArrayList<>();
//        try {
//            for (String link : ParseLink(url)) {
//                PageModel pageModel = new PageModel();
//                pageModel.setPath(link);
//              //  int index = resultBuff.indexOf(url);
//              //  if (index >= 0) {
//                  //  resultBuff.insert(index + url.length(), "\n" + "\t".repeat(level + 1) + link);
//
//           //     } else {
//              //      resultBuff.append(url + "\n");
//           //     }
//                SiteParse s1 = new SiteParse(link, level + 1);
//                listTask.add(s1);
//            }
//            invokeAll(listTask);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


