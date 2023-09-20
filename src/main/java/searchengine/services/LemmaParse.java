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

    public static Map<String, SiteModel> completeLemma;

    public LemmaParse copy() {
        return new LemmaParse(this.pageModelRepository,
                this.siteModelRepository,
                this.lemmaModelRepository,
                this.indexModelRepository);
    }

    @Transactional
    public void parsePage(SiteModel siteModel, PageModel pageModel, String htmlPage) {
        try {
            System.out.println("Зашёл в ParsePage - " + Thread.currentThread().getName() + " ************************** ");
            //  StringBuilder builder = new StringBuilder();
            LemmaFinder lemmaFinder = LemmaFinder.getInstance();
            Map<String, Integer> lemmas = lemmaFinder.collectLemmas(htmlPage);

            List<LemmaModel> listLemmaModel = new Vector<>();
            List<IndexModel> listIndexModel = new Vector<>();


//            Set<LemmaModel> setLemmaModel = new Vector<LemmaModel>();
//            Set<IndexModel> setIndexModel = new HashSet<>();
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {

                LemmaModel lemmaModel = new LemmaModel();
                IndexModel indexModel = new IndexModel();

                System.out.println("1 - " + entry.getKey() + " - " + pageModel.getPath() + " - " + Thread.currentThread().getName());
                if (completeLemma.containsKey(entry.getKey()) &&
                      //  completeLemma.get(entry.getKey()) == siteModel &&
                        lemmaModelRepository.existsByLemmaAndSiteModelId(entry.getKey(), siteModel)
                ) {
                    LemmaModel lemmaModelRep = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), siteModel);
                    lemmaModelRep.setFrequency(lemmaModelRep.getFrequency() + 1);
                    listLemmaModel.add(lemmaModelRep);
                    indexModel.setLemmaModelId(lemmaModelRep);

                    System.out.println("lol");

                    System.out.println("2 - " + entry.getKey() + " - " + pageModel.getPath());
                } else {
                    completeLemma.put(entry.getKey(), siteModel);
                    System.out.println("3 - " + entry.getKey() + " - " + pageModel.getPath());

                    lemmaModel.setLemma(entry.getKey());
                    lemmaModel.setFrequency(1);
                    lemmaModel.setSiteModelId(siteModel);
                    listLemmaModel.add(lemmaModel);

                    indexModel.setLemmaModelId(lemmaModel);
                }

                indexModel.setPageModelId(pageModel);
                indexModel.setRank(entry.getValue());
                listIndexModel.add(indexModel);
            }

            lemmaModelRepository.saveAllAndFlush(listLemmaModel);
            indexModelRepository.saveAllAndFlush(listIndexModel);


        } catch (
                Exception e) {
            System.out.println("Ошибка - parsePage" + pageModel.getPath());

            e.printStackTrace();
        }
    }



/*       try {
        //  StringBuilder builder = new StringBuilder();
        LemmaFinder lemmaFinder = LemmaFinder.getInstance();
        Map<String, Integer> lemmas = lemmaFinder.collectLemmas(htmlPage);
        Set<LemmaModel> setLemmaModel = new HashSet<>();
        Set<IndexModel> setIndexModel = new HashSet<>();
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()){
            LemmaModel lemmaModel = new LemmaModel();
            IndexModel indexModel = new IndexModel();
            System.out.println("1 - " + entry.getKey());

            // builder.append(entry.getKey());
            if (lemmaModelRepository.existByLemmaAndSiteModelId(entry.getKey(), siteModel)){
                LemmaModel lemmaModelRep = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), siteModel);
                lemmaModelRep.setFrequency(lemmaModelRep.getFrequency() + 1);
                setLemmaModel.add(lemmaModelRep);
                indexModel.setLemmaModelId(lemmaModelRep);

                System.out.println("lol");

                System.out.println("2 - " + entry.getKey());
            }else {
                System.out.println("3 - " + entry.getKey());

                lemmaModel.setLemma(entry.getKey());
                lemmaModel.setFrequency(1);
                lemmaModel.setSiteModelId(siteModel);
                setLemmaModel.add(lemmaModel);

                indexModel.setLemmaModelId(lemmaModel);
            }

            indexModel.setPageModelId(pageModel);
            indexModel.setRank(entry.getValue());
            setIndexModel.add(indexModel);

        }
        lemmaModelRepository.saveAllAndFlush(setLemmaModel);
        indexModelRepository.saveAllAndFlush(setIndexModel);

    } catch (Exception e) {
        System.out.println("Ошибка - parsePage" + pageModel.getPath() );

        e.printStackTrace();
    }*/


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
