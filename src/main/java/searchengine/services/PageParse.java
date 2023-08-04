package searchengine.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;

import java.io.IOException;
import java.lang.invoke.WrongMethodTypeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


@Getter
@Setter
@RequiredArgsConstructor
public class PageParse extends RecursiveAction {
    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;

    Integer siteId;
    String siteUrl;
    String page;
//
//    public PageParse(PageModelRepository pageModelRepository, SiteModelRepository siteModelRepository, Integer siteId, String siteUrl, String page) {
//        this.pageModelRepository = pageModelRepository;
//        this.siteModelRepository = siteModelRepository;
//        this.siteId = siteId;
//        this.siteUrl = siteUrl;
//        this.page = page;
//    }

    private Connection.Response response;
    public static List<String> linkSet = new Vector<>();


    public void addPage(){

    }



    public boolean CorrectUrl(String startLink, String link) {
        if (!link.isEmpty() && link.startsWith(startLink) &&
                link.length() > startLink.length() &&
                !link.equals(startLink) &&
                !link.contains("#") &&
                !link.contains(" ")// &&
               // !link.substring(20, link.length()).contains(".")
        ) {
            return true;
        }
        return false;
    }

    public List<String> ParseLink(String link) {
        List<String> outputList = new ArrayList<>();
        try {
            Thread.sleep(150);
            Document document = null;
            response = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) YandexIndexingMachine")
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .ignoreContentType(true)
                    .execute();
            if (!response.contentType().startsWith("text/html;")) {
                throw new WrongMethodTypeException("wrong format");
            }
            if (response.statusCode() != 200) throw new IOException(String.valueOf(response.statusCode()));

            document = response.parse();

            PageModel pageModel = new PageModel();
            pageModel.setCode(response.statusCode());
            System.out.println("+++++++++ " + siteUrl + " ++++++++++++++" );
            System.out.println("------------- " + siteModelRepository.findByUrl(siteUrl).getId() + " Вернул из базы по url");
            System.out.println("link: " + link);
            pageModel.setSiteModelId(siteModelRepository.findByUrl(siteUrl));
            pageModel.setPath(link);
            pageModel.setContent(String.valueOf(document));
            pageModelRepository.save(pageModel);

            Elements elements = document.select("a");

            for (Element ele : elements) {
                String linkString = new String(ele.attr("abs:href"));
                //System.out.println(linkString);
                if (CorrectUrl(link, linkString) && !linkString.equals(link) && !linkSet.contains(linkString)) {
                    linkSet.add(linkString);
                    outputList.add(linkString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputList;
    }

    @Override
    protected void compute() {
    //    System.out.println(page);
         List<PageParse> listTask = new Vector<>();
        try {
            for (String link : ParseLink(page)) {
                System.out.println("PageParse: " + link );
                PageParse pageParse = new PageParse(pageModelRepository, siteModelRepository);
                pageParse.setSiteId(siteId);
                pageParse.setSiteUrl(siteUrl);
                pageParse.setPage(link);
                listTask.add(pageParse);
            }
            invokeAll(listTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}






























/*    private final String url;
    private final String domain;
    private PageParse parent;
    private int level;
    private final SiteT siteT;
    private final PageTRepository pageTRepository;
    private final SiteTRepository siteTRepository;
   // private final ConcurrentMap<String, PageParse> uniqueLinks;
    public static List<String> linkSet = new Vector<>();
    private Connection.Response response = null;


    public PageParse(String url, PageParse parent) {
        this(url, parent.domain, parent.siteT, parent.pageModelRepository, parent.siteModelRepository, parent.uniqueLinks);
        this.parent = parent;
        this.level = parent.level + 1;
    }


    public boolean CorrectUrl(String startLink, String link) {
        if (!link.isEmpty() &&
                link.startsWith(startLink) &&
                link.length() > startLink.length() &&
                !link.contains("#") &&
                !link.contains(" ") &&
                !link.substring(startLink.length()).contains(".")
        ) {
            return true;
        }
        return false;
    }
    public List<String> ParseLink(String link) {
        System.out.println("ParseLink: " + link);
        List<String> outputList = new ArrayList<>();
        try {
            Thread.sleep(150);
            Document document = null;
            response = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) YandexIndexingMachine")
                    .referrer("http://www.google.com")
                    .timeout(5000)
                    .ignoreContentType(true)
                    .execute();
            if (!response.contentType().startsWith("text/html;")) {
                throw new WrongMethodTypeException("wrong format");
            }
            if (response.statusCode() != 200) throw new IOException(String.valueOf(response.statusCode()));
            document = response.parse();

            Elements elements = document.select("a");

            for (Element ele : elements) {
                String linkString = new String(ele.attr("abs:href"));
                System.out.println(linkString);
                if (CorrectUrl(link, linkString) && !linkString.equals(link) && !linkSet.contains(linkString)) {
                    linkSet.add(linkString);
                    outputList.add(linkString);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputList;
    }

    @Override
    protected void compute() {
        List<SiteParse> listTask = new ArrayList<>();
        try {
            for (String link : ParseLink(url)) {
                PageModel pageModel = new PageModel();
                pageModel.setPath(link);
                //  int index = resultBuff.indexOf(url);
                //  if (index >= 0) {
                //  resultBuff.insert(index + url.length(), "\n" + "\t".repeat(level + 1) + link);

                //     } else {
                //      resultBuff.append(url + "\n");
                //     }
                SiteParse s1 = new SiteParse(link, level + 1);
                listTask.add(s1);
            }
            invokeAll(listTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/










