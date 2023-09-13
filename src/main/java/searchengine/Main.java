/*
package searchengine;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class Main {
    private final LuceneMorphology luceneMorphology;
    private static final String WORD_TYPE_REGEX = "\\W\\w&&[^а-яА-Яa-zA-Z\\s]";
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

    public static void main(String[] args) throws IOException {
//        LuceneMorphology luceneMorph =
//                new RussianLuceneMorphology();
//        List<String> wordBaseForms =
//                luceneMorph.getNormalForms("леса");
//        wordBaseForms.forEach(System.out::println);
//
//        LuceneMorphology luceneMorph =
//                new EnglishLuceneMorphology();
//        List<String> wordBaseForms =
//                luceneMorph.getMorphInfo("we");
//        wordBaseForms.forEach(System.out::println);
//
//       // List<String> wordInfo = luceneMorphology.getMorphInfo(word);
//        for (String morphInfo : wordBaseForms) {
//            if (morphInfo.matches(WORD_TYPE_REGEX)) {
//                System.out.println("true");
//            }
//        }
//
//        String url = new String("https://www.skillbox.ru");
//
//        String res = UpdateUrl(url);
//        System.out.println(res);



//        Connection.Response response;
//        String link = "https://skillbox.ru";
//
//        Document document = null;
//        response = Jsoup.connect(link)
//                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) YandexIndexingMachine")
//                .referrer("http://www.google.com")
//                .timeout(5000)
//                .ignoreContentType(false)
//                .execute();
//
//        document = response.parse();
//
//        splitTextIntoWords(Jsoup.parse(document.html()).text());

    }

//    public static boolean isCorrectWordForm(String word) {
//        List<String> wordInfo = luceneMorphology.getMorphInfo(word);
//        for (String morphInfo : wordInfo) {
//            if (morphInfo.matches(WORD_TYPE_REGEX)) {
//                return false;
//            }
//        }
//        return true;
//    }

    public static String UpdateUrl(String url){
        if (url.endsWith("/")) {
            return new String(url.substring(0, url.length() - 1));
        }
        if (url.contains("www.")) {
            return new String(url.substring(0, 8) + url.substring(12));
        }
        return url;
    }

    public static List splitTextIntoWords(String text) {
        List<String> result = new Vector<>();
        String[] words = text.split("[[0-9],.;'\\s]+");
        for (int i = 0; i < words.length; i++){
            String word = (words[i].toLowerCase(Locale.ROOT)
                    .replaceAll("([^а-яa-z\\s])", "")
                    .trim());
            if (!word.isEmpty()){
                result.add(word);
            }

        }
        for (String op : result){
            System.out.println(op);
        }
        System.out.println(result);
        return result;
    }

//    private boolean isCorrectWordForm(String word) {
//        List<String> wordInfo = luceneMorphology.getMorphInfo(word);
//        for (String morphInfo : wordInfo) {
//            if (morphInfo.matches(WORD_TYPE_REGEX)) {
//                return false;
//            }
//        }
//        return true;
//    }

}
*/
