package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import org.xml.sax.InputSource;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchObj;
import searchengine.dto.search.SearchResponse;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexModelRepository;
import searchengine.repositories.LemmaModelRepository;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;
    private final LemmaModelRepository lemmaModelRepository;
    private final IndexModelRepository indexModelRepository;
    private List<String> queryLemmas = new ArrayList<>();


    @Override
    public SearchResponse search(String query, String site, Integer offset, Integer limit) { //offset - сдвиг от первого результата, limit - количество выводимых страниц
        SearchResponse searchResponse = new SearchResponse();
        queryLemmas = new ArrayList<>();
        try {

            SiteModel searchSite = existUpdateSite(site);

            Map<String, Integer> lemmas = LemmaFinder.getInstance().collectLemmas(query);
            Set<PageModel> setPageModelinDB = new LinkedHashSet<>();
            Map<PageModel, List<IndexModel>> mapPageIndexlinDB = new LinkedHashMap<>();
            List<LemmaModel> listLemmaModelInDb = new ArrayList<>();
            List<IndexModel> listIndexModelInDb = new ArrayList<>();
            Map<LemmaModel, List<IndexModel>> mapLemmaIndexs = new LinkedHashMap<>();
            Map<PageModel, List<IndexModel>> mapPageIndex = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                //Сайт не указан
                // Надо передать методу который разберёт лист и передаст объект с наименьшим frequency
                // Если нет сайта
                System.out.println(entry.getKey());
                if (searchSite == null) {
                    if ( lemmaModelRepository.existsByLemma(entry.getKey())) {
                        List<LemmaModel> listRepLemma = lemmaModelRepository.findAllByLemma(entry.getKey());
                        listRepLemma.sort(Comparator.comparing(LemmaModel::getFrequency));
                        listLemmaModelInDb.add(listRepLemma.get(0));
                    }
                    else {
                        continue;
                    }
                } else if (lemmaModelRepository.existsByLemmaAndSiteModelId(entry.getKey(), searchSite)){
                    LemmaModel lemmaModel = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), searchSite);
                    listLemmaModelInDb.add(lemmaModel);
                }




/*                if (site == null && lemmaModelRepository.existsByLemma(entry.getKey())) {
                    List<LemmaModel> listRepLemma = lemmaModelRepository.findAllByLemma(entry.getKey());
                    listRepLemma.sort(Comparator.comparing(LemmaModel::getFrequency));
*//*                    for (int z = 0; z < listRepLemma.size(); z++){
                        System.out.println(listRepLemma.get(z).getLemma() + " - " + listRepLemma.get(z).getFrequency());
                    }*//*
                    listLemmaModelInDb.add(listRepLemma.get(0));
                } else if (lemmaModelRepository.existsByLemmaAndSiteModelId(entry.getKey(), searchSite)) {
                    System.out.println("----------------------");
                    LemmaModel lemmaModel = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), searchSite);
                    listLemmaModelInDb.add(lemmaModel);
                } else {
                    continue;
                }*/

            }
            if (listLemmaModelInDb.isEmpty()) {
                searchResponse.setResult(false);
                searchResponse.setError("Указанная страница не найдена");
                return searchResponse;
            }


            // Сформиров и отсортирован лист от наименьшеньго к наибольшему по frequency lemma
            listLemmaModelInDb.sort(Comparator.comparing(LemmaModel::getFrequency));


            listIndexModelInDb.addAll(indexModelRepository.findAllByLemmaModelId(listLemmaModelInDb.get(0)));
            //Берём первую самую редку лемму
            listIndexModelInDb.forEach(indexModel -> {
                //Формирование списка сайтов по самой лемме с наименьшей frequency
                setPageModelinDB.add(indexModel.getPageModelId());
            });

            //Удаляем первую лемму по котрой нашли индексы и страницы
           // listLemmaModelInDb.remove(0);


            for (int i = 0; i < listLemmaModelInDb.size(); i++) {
                List<PageModel> removeList = new ArrayList<>();
                for (PageModel model : setPageModelinDB) {

                    List<IndexModel> otherList = indexModelRepository.findAllByLemmaModelIdAndPageModelId(listLemmaModelInDb.get(i), model);
                    System.out.println("Size otherList: " + otherList.size());
                    if (otherList.isEmpty()) {
//                            if (outMap.containsKey(model)){
//                                outMap.remove(model);
//                            }
                        removeList.add(model);
                    } else {
                        if (mapPageIndex.containsKey(model)) {
                            mapPageIndex.get(model).addAll(otherList);
                        }
                        else {
                            //Данная мапа является ключевой в расчёте релевантности
                            mapPageIndex.put(model, otherList);
                        }
                    }
                }
                setPageModelinDB.removeAll(removeList);
            }
            System.out.println("SizeFinalMapPageIndex: " + mapPageIndex.size());
            List<SearchObj> listObj = new ArrayList<>();
            float maxAbsRel = 0;
            Map<PageModel, Float> mapPageRel = new LinkedHashMap<>();
            //Начало рассчёта релевантности
            for (Map.Entry<PageModel, List<IndexModel>> entry : mapPageIndex.entrySet()){
                float rankPage = 0;
                    for (IndexModel indexModel : entry.getValue()){
                        rankPage += indexModel.getRank();
                    }
                    SearchObj searchObj = new SearchObj();
                    searchObj.setPageModel(entry.getKey());
                    searchObj.setListIndexModel(entry.getValue());
                    searchObj.setAbsRel(rankPage);
                    maxAbsRel = rankPage > maxAbsRel ? rankPage : maxAbsRel;
                    listObj.add(searchObj);

            }
                //Рассчитываем и записываем Относительную релевантность
            for ( int i = 0; i < listObj.size(); i++){
               float relRel = listObj.get(i).getAbsRel() / maxAbsRel;
               listObj.get(i).setRelRel(relRel);
                System.out.println("ins: " + relRel);
            }

            //Сортируем по Относительно релевантности
            Collections.sort(listObj, Collections.reverseOrder(Comparator.comparing(SearchObj::getRelRel)));


            for ( int i = 0; i < listObj.size(); i++){
                System.out.println(listObj.get(i).getRelRel());
            }




            Map<PageModel, Float>  outMap = new LinkedHashMap<>();
            for (PageModel pageModel : setPageModelinDB) {
                Set<IndexModel> setIndexForPage = new LinkedHashSet<>();
                for (Map.Entry<LemmaModel, List<IndexModel>> entry : mapLemmaIndexs.entrySet()) {
                    entry.getValue().forEach(indexModel -> {
                        if (indexModel.getPageModelId().equals(pageModel)) {
                            setIndexForPage.add(indexModel);
                        }
                    });
                }
                float relAbsPage = 0;
                for (IndexModel index : setIndexForPage) {
                    relAbsPage = relAbsPage + index.getRank();
                }
                outMap.put(pageModel, relAbsPage);
            }

            Map<PageModel, Float> resMap = outMap.entrySet().stream().sorted(Map.Entry.<PageModel, Float>comparingByValue().reversed()).
                    collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> a,
                    LinkedHashMap::new
            ));
                  //  forEach(System.out::println);
//                    collect(Collectors
//                            .toMap(Map.Entry::getKey,
//                                    Map.Entry::getValue,
//                                    (e1, e2) -> e1,
//                                    LinkedHashMap::new));


            List<SearchData> listSearchData = new ArrayList<>();
                for (int u = 0; u < listObj.size(); u++){
                    SearchData searchData = new SearchData();
                    searchData.setSite(listObj.get(u).getPageModel().getSiteModelId().getUrl());
                    searchData.setSiteName(listObj.get(u).getPageModel().getSiteModelId().getName());

                    String htmlText = listObj.get(u).getPageModel().getContent();
                    List<IndexModel> indexModels = listObj.get(u).getListIndexModel();
                   // Document doc = convertStringToDocument(htmlText);
                    Document doc = Jsoup.parse(htmlText);
                    Elements elements = doc.select("title");
//                    elements.forEach(element -> {
//                        System.out.println(element.text());
//                    });

                    searchData.setSnippet(createSnippet(htmlText, indexModels));
                    searchData.setTitle(elements.get(0).text());
                    searchData.setUri(listObj.get(u).getPageModel().getPath());
                    searchData.setRelevance(listObj.get(u).getRelRel());
                    listSearchData.add(searchData);

                }

            searchResponse.setResult(true);
            searchResponse.setCount(listObj.size());
            searchResponse.setData(listSearchData);




/*            Map<PageModel, List<IndexModel>> outMap = new LinkedHashMap<>();
            List<SearchData> listSearchData = new ArrayList<>();
            for (Map.Entry<PageModel, List<IndexModel>> entry : outMap.entrySet()) {

                SearchData searchData = new SearchData();
                searchData.setSite(entry.getKey().getSiteModelId().getUrl());
                searchData.setUri(entry.getKey().getPath().replace(entry.getKey().getSiteModelId().getUrl(), ""));
                searchData.setSiteName(entry.getKey().getSiteModelId().getName());
                searchData.setTitle("title");
                searchData.setSnippet("snippet");
                searchData.setRelevance(0.999);
                listSearchData.add(searchData);
                System.out.println(entry.getKey().getSiteModelId().getUrl() + " - " + entry.getKey().getPath());
                //Нужно заполнить SearchData и SearchResponse
            }
            searchResponse.setResult(true);
            searchResponse.setCount(outMap.size());
            searchResponse.setData(listSearchData);*/

        } catch (Exception e) {
            e.printStackTrace();
            searchResponse.setResult(false);
        }
        return searchResponse;
    }






    public String UpdateUrl(String url) {
//        if (url.endsWith("/")){
//            return new String(url.substring(0, url.length() - 1));
//        }
        if (url.contains("www.")) {
            return new String(url.substring(0, 8) + url.substring(12));
        }
        return url;
    }

    public SiteModel existUpdateSite(String site) {
        if (!(site == null)) {
            SiteModel searchSite = siteModelRepository.findByUrl(UpdateUrl(site));
            return searchSite;
        }
        return null;
    }

    public String createSnippet(String text, List<IndexModel> listIndexModel) throws IOException {
        StringBuilder result = new StringBuilder();


        for (IndexModel indexModel : listIndexModel){



        }



//        StringBuilder result = new StringBuilder();
//        for (IndexModel indexModel : listIndexModel){
//            System.out.println("----------");
//            System.out.println(indexModel.getLemmaModelId().getLemma());
//            int lemmalen = indexModel.getLemmaModelId().getLemma().length();
//            System.out.println(text);
//            System.out.println("**************");
//            System.out.println(text.indexOf("тренер"));
//            int startInd = text.lastIndexOf(indexModel.getLemmaModelId().getLemma());
//            int endInd = startInd + lemmalen;
//            int startTub = text.indexOf(" ", startInd - 10);
//            int endTub = text.indexOf("</", endInd);
//            System.out.println(startInd + "\n" + endInd + "\n" + endTub);
//            result.append("<b>" + text.substring(startInd, endInd) + "</b> " +
//                    text.substring(endInd, endTub) + "\n");
//            System.out.println("+++++++++++++");
//        }

        return result.toString();
    }

    public String createTitle(String text) throws IOException {
        //Elements head = doc
        return text.substring(text.indexOf("<title>") + 7, text.indexOf("</title>"));
    }


    public LemmaModel getLeastFrequency(List<LemmaModel> listLemmaModel) {


        return new LemmaModel();
    }

//    private static Document convertStringToDocument(String xmlStr) {
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder;
//        try
//        {
//            builder = factory.newDocumentBuilder();
//            Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
//            return doc;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
//    public String getAt(String st, int pos) {
//        StringBuilder sb = new StringBuilder();
//        String[] tokens = st.split(" ");
//
//        int pre = 10;
//        int post = 10;
//        if (pos < pre) {
//            pre = pos;
//            post = post + 10 - pos;
//        }
//        if (pos > tokens.length - post) {
//            post = tokens.length - post - 1;
//            pre = pre + (10 - post);
//        }
//
//        for (int i = pos - pre; i < pos + post; i++) {
//            if (lemmaFinder.collectLemmas(tokens[i]).size() > 0 &&
//                    queryLemmas.contains(lemmaFinder.collectLemmas(tokens[i]).keySet().stream().findFirst().orElse(""))) {
//                sb.append("<b>").append(tokens[i]).append("</b>");
//            } else {
//                sb.append(tokens[i]);
//            }
//            sb.append(" ");
//        }
//
//        return sb.toString();
//    }


