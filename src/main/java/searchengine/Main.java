package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
//        LuceneMorphology luceneMorph =
//                new RussianLuceneMorphology();
//        List<String> wordBaseForms =
//                luceneMorph.getNormalForms("или");
//        wordBaseForms.forEach(System.out::println);

//        LuceneMorphology luceneMorph =
//                new RussianLuceneMorphology();
//        List<String> wordBaseForms =
//                luceneMorph.getMorphInfo("лес");
//        wordBaseForms.forEach(System.out::println);
//
//        String url = new String("https://www.skillbox.ru");
//
//        String res = UpdateUrl(url);
//        System.out.println(res);
        Connection.Response response;
        String link = "https://skillbox.ru";


        Document document = null;
        response = Jsoup.connect(link)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) YandexIndexingMachine")
                .referrer("http://www.google.com")
                .timeout(5000)
                .ignoreContentType(false)
                .execute();

        document = response.parse();

        System.out.println(document.body());
    }

    public static String UpdateUrl(String url){
        if (url.endsWith("/")) {
            return new String(url.substring(0, url.length() - 1));
        }
        if (url.contains("www.")) {
            return new String(url.substring(0, 8) + url.substring(12));
        }
        return url;
    }
}
