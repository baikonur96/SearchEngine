package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.jsoup.nodes.Document;
import searchengine.config.Site;
import searchengine.config.SitesList;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

@RequiredArgsConstructor
@Service

public class SiteParse extends RecursiveTask<StringBuffer> {

/*    SitesList sites;
    List<Site> siteList = sites.getSites();*/

    public StringBuffer resultBuff = new StringBuffer();
    public List<String> linkSet = new Vector<>();
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
        if (!link.isEmpty() && link.startsWith(startLink) &&
                link.length() > startLink.length() &&
            //    !link.contains("https://www.lenta.ru") &&
                !link.contains("#") &&
                !link.contains(" ") &&
                !link.substring(20, link.length()).contains(".")
        ) {
            return true;
        }
        return false;
    }

    public List<String> ParseLink(String link) {
        List<String> outputList = new ArrayList<>();
        try {
            Thread.sleep(150);
            Document document = (Document) Jsoup.connect(link)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) YandexIndexingMachine")
                    .referrer("http://www.google.com")
                    .ignoreContentType(true)
                    .get();
            Elements elements = document.select("a");
            for (Element ele : elements) {
                String linkString = new String(ele.attr("abs:href"));
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
    protected StringBuffer compute() {
        List<SiteParse> listTask = new ArrayList<>();
        try {
            for (String link : ParseLink(url)) {
                System.out.println(link);
                int index = resultBuff.indexOf(url);
                if (index >= 0) {
                    resultBuff.insert(index + url.length(), "\n" + "\t".repeat(level + 1) + link);

                } else {
                    resultBuff.append(url + "\n");
                }
                SiteParse s1 = new SiteParse(link, level + 1);
                System.out.println(s1);
                listTask.add(s1);
            }
            invokeAll(listTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBuff;
    }

}
