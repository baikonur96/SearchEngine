package searchengine;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
//        LuceneMorphology luceneMorph =
//                new RussianLuceneMorphology();
//        List<String> wordBaseForms =
//                luceneMorph.getNormalForms("или");
//        wordBaseForms.forEach(System.out::println);

        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        List<String> wordBaseForms =
                luceneMorph.getMorphInfo("лес");
        wordBaseForms.forEach(System.out::println);

        String url = new String("https://www.skillbox.ru");

        String res = UpdateUrl(url);
        System.out.println(res);


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
