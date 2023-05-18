package searchengine.services;


import java.util.List;
import java.util.Vector;
import java.util.concurrent.RecursiveTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class StartIndexingService  {

    public static StringBuffer resultBuff = new StringBuffer(); //ApiController.START_URL
    public static List<String> linkSet = new Vector<>();

    String url;
    int level;

    public StartIndexingService(String url, int level) {
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
              //  !link.equals(Main.START_URL) &&
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
            Document document = Jsoup.connect(link).get();
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
        List<StartIndexingService> listTask = new ArrayList<>();
        try {
            for (String link : ParseLink(url)) {
                int index = resultBuff.indexOf(url);
                if (index >= 0) {
                    resultBuff.insert(index + url.length(), "\n" + "\t".repeat(level + 1) + link);
                } else {
                    resultBuff.append(url + "\n");
                }
                StartIndexingService s1 = new StartIndexingService(link, level + 1);
                listTask.add(s1);
            }
            invokeAll(listTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultBuff;
    }
}

