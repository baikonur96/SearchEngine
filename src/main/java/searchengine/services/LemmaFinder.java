package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveAction;


public class LemmaFinder  {
    private final LuceneMorphology morphologyRus;
    private final LuceneMorphology morphologyEng;
    private static final String WORD_TYPE_REGEX = "\\W\\w&&[^а-яА-Я\\s]";
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ", "CONJ", "INT", "PREP", "ARTICLE", "PART"};


    public static LemmaFinder getInstance()  {
        try {
            LuceneMorphology morphologyEng = new EnglishLuceneMorphology();
            LuceneMorphology morphologyRus = new RussianLuceneMorphology();
            return new LemmaFinder(morphologyRus, morphologyEng);
        }catch (Exception e){

        }
        return null;
    }
//
    private LemmaFinder(LuceneMorphology luceneMorphologyRus, LuceneMorphology luceneMorphologyEng) {
        this.morphologyRus = luceneMorphologyRus;
        this.morphologyEng = luceneMorphologyEng;
    }

    public LemmaFinder(){
        throw new IllegalArgumentException("Disallow construct");
    }




    // * Метод разделяет текст на слова, находит все леммы и считает их количество.
    // *
   //  * @param text текст из которого будут выбираться леммы
   //  * @return ключ является леммой, а значение количеством найденных лемм

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
        return result;
    }


    public Map<String, Integer> collectLemmas(String textHtml) {
        LuceneMorphology luceneMorphology;
        Map<String, Integer> lemmas = new Hashtable<>();
        List<String> words = splitTextIntoWords(Jsoup.parse(textHtml).text());
        for (String word : words) {
           // System.out.println(word + " - " + word.length());
            if (word.isBlank() || (word.trim().length() < 2)) {
                continue;
            }
            if (isRussian(word)){
                luceneMorphology = morphologyRus;
            } else {
                luceneMorphology = morphologyEng;
            }

//            if (morphInfo.matches(WORD_TYPE_REGEX)) {
//                return false;
//            }
            try {
                List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
                if (anyWordBaseBelongToParticle(wordBaseForms)) {
                    continue;
                }
            }catch (Exception e){
                continue;
            }

            List<String> normalForms = luceneMorphology.getNormalForms(word);
            if (normalForms.isEmpty()) {
                continue;
            }

            String normalWord = normalForms.get(0);

            if (lemmas.containsKey(normalWord)) {
                lemmas.put(normalWord, lemmas.get(normalWord) + 1);
            } else {
                lemmas.put(normalWord, 1);
            }
        }

        return lemmas;
    }



     // @param text текст из которого собираем все леммы
            //@return набор уникальных лемм найденных в тексте


//    public Set<String> getLemmaSet(String text) {
//        String[] textArray = arrayContainsRussianWords(text);
//        Set<String> lemmaSet = new HashSet<>();
//        for (String word : textArray) {
//            if (!word.isEmpty() && isCorrectWordForm(word)) {
//                List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
//                if (anyWordBaseBelongToParticle(wordBaseForms)) {
//                    continue;
//                }
//                lemmaSet.addAll(luceneMorphology.getNormalForms(word));
//            }
//        }
//        return lemmaSet;
//    }

    private boolean anyWordBaseBelongToParticle(List<String> wordBaseForms) {
        return wordBaseForms.stream().anyMatch(this::hasParticleProperty);
    }

    private boolean hasParticleProperty(String wordBase) {
        for (String property : particlesNames) {
            if (wordBase.toUpperCase().contains(property)) {
                return true;
            }
        }
        return false;
    }

//    private String[] arrayContainsRussianWords(String text) {
//        return text.toLowerCase(Locale.ROOT)
//                .replaceAll("([^а-яa-z\\s])", " ")
//                .trim()
//                .split("\\s+");
//    }

    private static boolean isRussian(String word) {
        return word.chars()
                .mapToObj(Character.UnicodeBlock::of)
                .anyMatch(Character.UnicodeBlock.CYRILLIC::equals);
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
