package searchengine.services;

import lombok.RequiredArgsConstructor;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LemmaParse {

    private final PageModelRepository pageTRepository;
    private final SiteModelRepository siteTRepository;
    private final LemmaModelRepository lemmaTRepository;
    private final IndexModelRepository indexTRepository;


    public LemmaParse copy() {
        return new LemmaParse(this.pageTRepository,
                this.siteTRepository,
                this.lemmaTRepository,
                this.indexTRepository);
    }


    public void parsePage() {
        try {
            System.out.println("lol");
          //  System.out.println(siteModel.getName());
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
