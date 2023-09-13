package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexModelRepository;
import searchengine.repositories.LemmaModelRepository;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LemmaParse {

    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;
    private final LemmaModelRepository lemmaModelRepository;
    private final IndexModelRepository indexModelRepository;


    public LemmaParse copy() {
        return new LemmaParse(this.pageModelRepository,
                this.siteModelRepository,
                this.lemmaModelRepository,
                this.indexModelRepository);
    }

    @Transactional
    public void parsePage(SiteModel siteModel, PageModel pageModel, String htmlPage) {
        try {
            LemmaFinder lemmaFinder = LemmaFinder.getInstance();
            Map<String, Integer> lemmas = lemmaFinder.collectLemmas(htmlPage);
            Set<LemmaModel> setLemmaModel = new HashSet<>();
            Set<IndexModel> setIndexModel = new HashSet<>();
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()){
                LemmaModel lemmaModel = new LemmaModel();
                System.out.println("1 - " + entry.getKey());
                if (lemmaModelRepository.existsByLemma(entry.getKey())){
                    LemmaModel existLemmaModel = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), siteModel);
                    existLemmaModel.setFrequency(existLemmaModel.getFrequency() + 1);
                    lemmaModelRepository.save(existLemmaModel);
                    System.out.println("lol");
                    System.out.println("2 - " + entry.getKey());
                    continue;
                }
                System.out.println("3 - " + entry.getKey());
               // LemmaModel lemma = lemmaModelRepository.findByLemmaAndSiteModel(entry.getKey(), siteModel);

                lemmaModel.setLemma(entry.getKey());
                lemmaModel.setFrequency(1);
                lemmaModel.setSiteModelId(siteModel);
                setLemmaModel.add(lemmaModel);
                IndexModel indexModel = new IndexModel();
                indexModel.setLemmaModelId(lemmaModel);
                indexModel.setPageModelId(pageModel);
                indexModel.setRank(entry.getValue());
                setIndexModel.add(indexModel);
               // lemmaModelRepository.save(lemmaModel);
               // System.out.println(siteModel.getName() + " - " + pageModel.getPath() + " - " + entry.getKey());

            }

            lemmaModelRepository.saveAll(setLemmaModel);
            indexModelRepository.saveAll(setIndexModel);


//            System.out.println(siteModel.getName());
//            LemmaFinder l = LemmaFinder.getInstance();
//            Map<String, Integer> lemmaMap = l.collectLemmas(pageT.getContent());
//            Map<LemmaModel, Integer> lemmaModelList = new HashMap<>();
//            List<IndexModel> indexTList = new ArrayList<>();
//            lemmaMap.entrySet().forEach(lemma -> lemmaModelList.put(parseLemma(lemma.getKey()), lemma.getValue()));
//            lemmaTRepository.saveAll(lemmaModelList.keySet());
//            lemmaModelList.entrySet().forEach(e -> indexTList.add(new IndexT(pageModel.get(), e.getKey().getLemmaId(), e.getValue())));
//            indexTRepository.saveAll(indexTList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public LemmaModel parseLemma(String lemmaText) {
//        LemmaModel result = lemmaTRepository.findByLemmaAndSiteModelBySiteId(lemmaText, siteModel);
//        if (result == null) {
//            result = new LemmaModel(siteModel.getId(), lemmaText, 1);
//        } else {
//            result.setFrequency(result.getFrequency() + 1);
//        }
//        return result;
//    }


//    public void setSiteModel(SiteModel siteT) {
//        this.siteModel = siteT;
//    }

}
