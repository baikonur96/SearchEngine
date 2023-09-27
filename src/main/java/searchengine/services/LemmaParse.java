package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import searchengine.config.Site;
import searchengine.model.*;
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void parsePage(SiteModel siteModel, PageModel pageModel, String htmlPage) {
        try {
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
                if (lemmaModelRepository.existsByLemmaAndSiteModelId(entry.getKey(), siteModel)){
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
        }


















/*        try {
            System.out.println("Зашёл в ParsePage - " + Thread.currentThread().getName() + " ************************** ");
            //  StringBuilder builder = new StringBuilder();
            LemmaFinder lemmaFinder = LemmaFinder.getInstance();
            Map<String, Integer> lemmas = lemmaFinder.collectLemmas(htmlPage);



            Set<LemmaModel> listLemmaModel = new HashSet<>();
            List<IndexModel> listIndexModel = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                IndexModel indexModel = new IndexModel();
                LemmaModel lemmaModel = new LemmaModel();

                lemmaModel.setSiteModelId(siteModel);
                lemmaModel.setLemma(entry.getKey());
                lemmaModel.setFrequency(1);

//              lemmaModel.setLemma(entry.getKey());

//              lemmaModel.setSiteModelId(siteModel);
                listLemmaModel.add(lemmaModel);

                indexModel.setLemmaModelId(lemmaModel);
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
        }*/
    }

//    public LemmaModel parseLemma(String lemmaText) {
//        LemmaModel result = lemmaModelRepository.findByLemmaAndSiteModelId(lemmaText, siteModel);
//        if (result == null) {
//            result = new LemmaModel(siteModel, lemmaText, 1);
//        } else {
//            result.setFrequency(result.getFrequency() + 1);
//        }
//        return result;
//    }
//
//    public void setSiteT(SiteModel siteModel) {
//        this.siteModel = siteModel;
//    }


}






//            List<LemmaModel> listLemmaModel = new Vector<>();
//            List<IndexModel> listIndexModel = new Vector<>();
//            Set<LemmaModel> setLemmaModel = new Vector<LemmaModel>();
//            Set<IndexModel> setIndexModel = new HashSet<>();
/*
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                LemmaModel lemmaModel = new LemmaModel();
                IndexModel indexModel = new IndexModel();

                System.out.println("1 - " + entry.getKey() + " - " + pageModel.getPath() + " - " + Thread.currentThread().getName());

                if (lemmaModelRepository.existsByLemmaAndSiteModelId(entry.getKey(), siteModel)) {
                    LemmaModel lemmaModelRep = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), siteModel);
                    lemmaModelRep.setFrequency(lemmaModelRep.getFrequency() + 1);
                    //  listLemmaModel.add(lemmaModelRep);
                    lemmaModelRepository.saveAndFlush(lemmaModelRep);
                    //  indexModel.setLemmaModelId(lemmaModelRep);

                    System.out.println("lol");

                    System.out.println("2 - " + entry.getKey() + " - " + pageModel.getPath());


//                    if (completeLemma.containsKey(entry.getKey()) &&
//                            completeLemma.get(entry.getKey()) == siteModel ){
//                        LemmaModel lemmaModelRep = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), siteModel);
//                        lemmaModelRep.setFrequency(lemmaModelRep.getFrequency() + 1);
//                        listLemmaModel.add(lemmaModelRep);
//                        indexModel.setLemmaModelId(lemmaModelRep);
//
//                        System.out.println("lol");
//
//                        System.out.println("2 - " + entry.getKey() + " - " + pageModel.getPath());
//                    } else {
//
//                    }

                } else {
                    //  completeLemma.put(entry.getKey(), siteModel);

                    lemmaModel.setLemma(entry.getKey());
                    lemmaModel.setFrequency(1);
                    lemmaModel.setSiteModelId(siteModel);
                    //listLemmaModel.add(lemmaModel);
                    indexModel.setLemmaModelId(lemmaModel);
                    lemmaModelRepository.saveAndFlush(lemmaModel);
                }
                indexModel.setPageModelId(pageModel);
                indexModel.setRank(entry.getValue());
                indexModelRepository.saveAndFlush(indexModel);
            }
*/

            //   lemmaModelRepository.saveAllAndFlush(listLemmaModel);
            //   indexModelRepository.saveAllAndFlush(listIndexModel);









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


