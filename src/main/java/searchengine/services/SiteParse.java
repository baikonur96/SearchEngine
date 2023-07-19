package searchengine.services;

import lombok.RequiredArgsConstructor;
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
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;


@Service
@RequiredArgsConstructor
public class SiteParse extends RecursiveAction {

    @Autowired
    private PageModelRepository pageModelRepository;
    public static StringBuffer resultBuff = new StringBuffer("https://skillbox.ru/");
    public static List<String> linkSet = new Vector<>();
    String url;
    int level;

    public SiteParse(String url, int level) {
        this.url = url;
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
            Document document = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) YandexIndexingMachine")
                    .referrer("http://www.google.com")
                    .ignoreContentType(true)
                    .get();
            Elements elements = document.select("a");

            for (Element ele : elements) {
               // System.out.println(ele);
               // System.out.println("ParseLink + element: " + ele);
                String linkString = new String(ele.attr("abs:href"));

                System.out.println(linkString);
                if (CorrectUrl(link, linkString) && !linkString.equals(link) && !linkSet.contains(linkString)) {
                    System.out.println("!!!!OK!!!!!! - " + linkString);
                    linkSet.add(linkString);
                    outputList.add(linkString);
                    System.out.println("Размер листа: " + outputList.size());
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
                System.out.println("Найден и выведен итогово - " + link);
              //  int index = resultBuff.indexOf(url);
              //  if (index >= 0) {
                  //  resultBuff.insert(index + url.length(), "\n" + "\t".repeat(level + 1) + link);

           //     } else {
              //      resultBuff.append(url + "\n");
           //     }
                SiteParse s1 = new SiteParse(link, level + 1);
                System.out.println(s1);
//                PageModel pageModel = new PageModel();
//                    pageModel.setCode();
                listTask.add(s1);
            }
            invokeAll(listTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
