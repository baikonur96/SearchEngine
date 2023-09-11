package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveAction;

@Slf4j
@Service
public class LemmaFinder extends RecursiveAction {
   // private final LuceneMorphology morphologyRus;
  //  private final LuceneMorphology morphologyEng;
    private static final String WORD_TYPE_REGEX = "\\W\\w&&[^а-яА-Я\\s]";
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};

//    public static void getInstance() throws IOException {
//        LuceneMorphology morphologyEng = new EnglishLuceneMorphology();
//        LuceneMorphology morphologyRus = new RussianLuceneMorphology();
//    }
//
//    private LemmaFinder(LuceneMorphology luceneMorphologyRus, LuceneMorphology luceneMorphologyEng) {
//        this.morphologyRus = luceneMorphologyRus;
//        this.morphologyEng = luceneMorphologyEng;
//    }

//    public LemmaFinder(){
//        throw new RuntimeException("Disallow construct");
//    }

    @Override
    protected void compute() {

        for (int i = 0; i<100000000; i++){
            System.out.println("Второй поток");
        }

    }


    // * Метод разделяет текст на слова, находит все леммы и считает их количество.
    // *
   //  * @param text текст из которого будут выбираться леммы
   //  * @return ключ является леммой, а значение количеством найденных лемм


//    public Map<String, Integer> collectLemmas(String text) {
//        HashMap<String, Integer> lemmas = new HashMap<>();
//
//        for (String word : words) {
//            if (word.isBlank()) {
//                continue;
//            }
//
//            List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
//            if (anyWordBaseBelongToParticle(wordBaseForms)) {
//                continue;
//            }
//
//            List<String> normalForms = luceneMorphology.getNormalForms(word);
//            if (normalForms.isEmpty()) {
//                continue;
//            }
//
//            String normalWord = normalForms.get(0);
//
//            if (lemmas.containsKey(normalWord)) {
//                lemmas.put(normalWord, lemmas.get(normalWord) + 1);
//            } else {
//                lemmas.put(normalWord, 1);
//            }
//        }
//
//        return lemmas;
//    }



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

    private String[] arrayContainsRussianWords(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("([^а-яa-z\\s])", " ")
                .trim()
                .split("\\s+");
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
