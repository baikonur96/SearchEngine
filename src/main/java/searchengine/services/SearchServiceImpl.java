package searchengine.services;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import searchengine.dto.search.Page;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchObj;
import searchengine.dto.search.SearchResponse;
import searchengine.model.*;
import searchengine.repositories.IndexModelRepository;
import searchengine.repositories.LemmaModelRepository;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private static final double THRESHOLD = 0.8;
    private final PageModelRepository pageModelRepository;
    private final SiteModelRepository siteModelRepository;
    private final LemmaModelRepository lemmaModelRepository;
    private final IndexModelRepository indexModelRepository;

    //private static final Logger logger = LoggerFactory.getLogger(SearchServiceImpl.class);


    @Override
    public SearchResponse search(String query, String site, Integer offset, Integer limit) {
        if (query == null || query.isEmpty()) {
            throw new RuntimeException("Empty query");
        }
        if (offset < 0) {
            throw new RuntimeException("Negative Offset");
        }
        if (limit < 0) {
            throw new RuntimeException("Negative limit");
        }
        if (site != null) {
            return searchBySite(query, site, offset, limit);
        } else {
            return searchByAllSites(query, site, offset, limit);
        }
    }

    private SearchResponse searchBySite(String query, String site, Integer offset, Integer limit) {
        log.info("The «{}» search query has been started", query);

        return null;
    }

    private SearchResponse searchByAllSites(String query, String site, Integer offset, Integer limit) {
        log.info("The «{}» search query has been started", query);
        List<SiteModel> siteModelList = siteModelRepository.findByStatus(StatusOption.INDEXED);
        if (siteModelList.isEmpty()) {
            throw new RuntimeException("No indexed sites found");
        }

       Set<String>  queryLemmas = LemmaFinder.getInstance().collectLemmas(query).keySet(); //Получаем Сет Стринговых запрашиваемых лемм
        Map<Integer, SiteModel> foundSites = new HashMap<>();
        List<Page> foundPages = new ArrayList<>();

        for (SiteModel siteObj : siteModelList){
            // Идём по сайтам которые проиндексированы

            //Более ранний подход
//            lemmaModelRepository.existsByLemmaAndSiteModelId(queryLemmas., siteObj)){
//            LemmaModel lemmaModel = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), searchSite);
//            listLemmaModelInDb.add(lemmaModel);
//
//                listLemmaModelInDb.sort(Comparator.comparing(LemmaModel::getFrequency));

            List<Page> foundPagesBySite = findPagesBySite(siteObj, queryLemmas);
            if (foundPagesBySite.isEmpty()) {
                continue;
            }
            foundPages.addAll(foundPagesBySite);
            foundSites.put(siteObj.getId(), siteObj);


        }

        if (foundPages.isEmpty()) {
            return new SearchResponse(true, 0, Collections.emptyList());
        }

        List<SearchData> dataList = createSearchDataList(foundSites, foundPages, offset, limit, queryLemmas); //Отдаём формировать отдельный основной ответ
        log.info("The «{}» query has been executed", query);
        return new SearchResponse(true, foundPages.size(), dataList);
    }

    private List<SearchData> createSearchDataList(Map<Integer, SiteModel> foundSites,
                                                  List<Page> foundPages,
                                                  Integer offset, Integer limit,
                                                  Set<String>  queryLemmas) {
        return null;
    }


    @Transactional
    public List<Page> findPagesBySite(SiteModel site, Set<String> queryLemmaSet) {
        List<LemmaModel> filteredLemmas = getFilteredLemmas(queryLemmaSet, site);
        if (filteredLemmas.isEmpty()) {
            return Collections.emptyList();
        }
        filteredLemmas.sort(Comparator.comparing(LemmaModel::getFrequency)); //Сортируем полученный лист по частоте
        //Сортировать леммы в порядке увеличения частоты встречаемости
        //(по возрастанию значения поля frequency) — от самых редких до
        //самых частых.
        List<Integer> pageIds = searchPageIds(filteredLemmas); //Получение списка всех page данных демм с определённого site
        if (pageIds.isEmpty()) {
            return Collections.emptyList();
        }
        return getSitePageList(pageIds, filteredLemmas);
    }


    //Преобразуем Стринг звпросов в леммы определённого сайта
    private List<LemmaModel> getFilteredLemmas(Set<String> queryLemmaSet, SiteModel site) {
        int pageCount = pageModelRepository.countBySiteModelId(site);
        int siteThreshold = (int) (pageCount * THRESHOLD);
        return null;
       // return lemmaModelRepository.findBySiteAndLemmaInAndFrequencyLessThanEqual(site, queryLemmaSet, siteThreshold);
    }

    private List<Integer> searchPageIds(List<LemmaModel> filteredLemmas) {
        int lemmaId = filteredLemmas.get(0).getId();
        List<Integer> cumulativePageIds = new ArrayList<>(); //new LinkedList<>(indexModelRepository.findPageIdsByLemmaId(lemmaId));
        for (int i = 1; i < filteredLemmas.size(); ++i) {
            lemmaId = filteredLemmas.get(i).getId();
            List<Integer> pageIds = new ArrayList<>();//  indexModelRepository.findPageIdsByLemmaId(lemmaId);
            cumulativePageIds.retainAll(pageIds);
            if (cumulativePageIds.isEmpty()) {
                return Collections.emptyList();
            }
        }
        return cumulativePageIds;
    }

    private List<Page> getSitePageList(List<Integer> pageIds, List<LemmaModel> filteredLemmas) {
        List<Page> pageList = calculateAbsoluteRelevancePages(pageIds, filteredLemmas);

        double maxRelevance = pageList.stream().mapToDouble(Page::getRelevance).max().orElse(1.0);
        for (Page page : pageList) {
            page.setRelevance(page.getRelevance() / maxRelevance);
        }
        return pageList;
    }

    private List<Page> calculateAbsoluteRelevancePages(List<Integer> pageIds, List<LemmaModel> filteredLemmas) {
        return null;
//        return indexModelRepository.getRankSumByPageIdsAndLemmaIds(pageIds, //суммирует все rank.lemma для опред page
//                filteredLemmas.stream().map(LemmaModel::getId).toList());
   }





//    public SearchResponse searchOld(String query, String site, Integer offset, Integer limit) {
//        //offset - сдвиг от первого результата, limit - количество выводимых страниц
//        SearchResponse searchResponse = new SearchResponse();
//        queryLemmas = new ArrayList<>();
//        try {
//
//            SiteModel searchSite = existUpdateSite(site);
//
//
//            Map<String, Integer> lemmas = LemmaFinder.getInstance().collectLemmas(query);
//            Set<PageModel> setPageModelinDB = new LinkedHashSet<>();
//            Map<PageModel, List<IndexModel>> mapPageIndexlinDB = new LinkedHashMap<>();
//            List<LemmaModel> listLemmaModelInDb = new ArrayList<>();
//            List<IndexModel> listIndexModelInDb = new ArrayList<>();
//            Map<LemmaModel, List<IndexModel>> mapLemmaIndexs = new LinkedHashMap<>();
//            Map<PageModel, List<IndexModel>> mapPageIndex = new LinkedHashMap<>();
//            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
//                //Сайт не указан
//                // Надо передать методу который разберёт лист и передаст объект с наименьшим frequency
//                // Если нет сайта
//                System.out.println(entry.getKey());
//                if (searchSite == null) {
//                    if ( lemmaModelRepository.existsByLemma(entry.getKey())) {
//                        List<LemmaModel> listRepLemma = lemmaModelRepository.findAllByLemma(entry.getKey());
//                        listRepLemma.sort(Comparator.comparing(LemmaModel::getFrequency));
//                        listLemmaModelInDb.add(listRepLemma.get(0));
//                    }
//                    else {
//                        continue;
//                    }
//                } else if (lemmaModelRepository.existsByLemmaAndSiteModelId(entry.getKey(), searchSite)){
//                    LemmaModel lemmaModel = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), searchSite);
//                    listLemmaModelInDb.add(lemmaModel);
//                }
//
//
//
//
///*                if (site == null && lemmaModelRepository.existsByLemma(entry.getKey())) {
//                    List<LemmaModel> listRepLemma = lemmaModelRepository.findAllByLemma(entry.getKey());
//                    listRepLemma.sort(Comparator.comparing(LemmaModel::getFrequency));
//*//*                    for (int z = 0; z < listRepLemma.size(); z++){
//                        System.out.println(listRepLemma.get(z).getLemma() + " - " + listRepLemma.get(z).getFrequency());
//                    }*//*
//                    listLemmaModelInDb.add(listRepLemma.get(0));
//                } else if (lemmaModelRepository.existsByLemmaAndSiteModelId(entry.getKey(), searchSite)) {
//                    System.out.println("----------------------");
//                    LemmaModel lemmaModel = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), searchSite);
//                    listLemmaModelInDb.add(lemmaModel);
//                } else {
//                    continue;
//                }*/
//
//            }
//            if (listLemmaModelInDb.isEmpty()) {
//                searchResponse.setResult(false);
//                searchResponse.setError("Указанная страница не найдена");
//                return searchResponse;
//            }
//
//
//            // Сформиров и отсортирован лист от наименьшеньго к наибольшему по frequency lemma
//            listLemmaModelInDb.sort(Comparator.comparing(LemmaModel::getFrequency));
//
//
//            listIndexModelInDb.addAll(indexModelRepository.findAllByLemmaModelId(listLemmaModelInDb.get(0)));
//            //Берём первую самую редку лемму
//            listIndexModelInDb.forEach(indexModel -> {
//                //Формирование списка сайтов по самой лемме с наименьшей frequency
//                setPageModelinDB.add(indexModel.getPageModelId());
//            });
//
//            //Удаляем первую лемму по котрой нашли индексы и страницы
//           // listLemmaModelInDb.remove(0);
//
//
//            for (int i = 0; i < listLemmaModelInDb.size(); i++) {
//                List<PageModel> removeList = new ArrayList<>();
//                for (PageModel model : setPageModelinDB) {
//
//                    List<IndexModel> otherList = indexModelRepository.findAllByLemmaModelIdAndPageModelId(listLemmaModelInDb.get(i), model);
//                    System.out.println("Size otherList: " + otherList.size());
//                    if (otherList.isEmpty()) {
////                            if (outMap.containsKey(model)){
////                                outMap.remove(model);
////                            }
//                        removeList.add(model);
//                    } else {
//                        if (mapPageIndex.containsKey(model)) {
//                            mapPageIndex.get(model).addAll(otherList);
//                        }
//                        else {
//                            //Данная мапа является ключевой в расчёте релевантности
//                            mapPageIndex.put(model, otherList);
//                        }
//                    }
//                }
//                setPageModelinDB.removeAll(removeList);
//            }
//            System.out.println("SizeFinalMapPageIndex: " + mapPageIndex.size());
//            List<SearchObj> listObj = new ArrayList<>();
//            float maxAbsRel = 0;
//            Map<PageModel, Float> mapPageRel = new LinkedHashMap<>();
//            //Начало рассчёта релевантности
//            for (Map.Entry<PageModel, List<IndexModel>> entry : mapPageIndex.entrySet()){
//                float rankPage = 0;
//                    for (IndexModel indexModel : entry.getValue()){
//                        rankPage += indexModel.getRank();
//                    }
//                    SearchObj searchObj = new SearchObj();
//                    searchObj.setPageModel(entry.getKey());
//                    searchObj.setListIndexModel(entry.getValue());
//                    searchObj.setAbsRel(rankPage);
//                    maxAbsRel = rankPage > maxAbsRel ? rankPage : maxAbsRel;
//                    listObj.add(searchObj);
//
//            }
//                //Рассчитываем и записываем Относительную релевантность
//            for ( int i = 0; i < listObj.size(); i++){
//               float relRel = listObj.get(i).getAbsRel() / maxAbsRel;
//               listObj.get(i).setRelRel(relRel);
//                System.out.println("ins: " + relRel);
//            }
//
//            //Сортируем по Относительно релевантности
//            Collections.sort(listObj, Collections.reverseOrder(Comparator.comparing(SearchObj::getRelRel)));
//
//
//            for ( int i = 0; i < listObj.size(); i++){
//                System.out.println(listObj.get(i).getRelRel());
//            }
//
//
//
//
//            Map<PageModel, Float>  outMap = new LinkedHashMap<>();
//            for (PageModel pageModel : setPageModelinDB) {
//                Set<IndexModel> setIndexForPage = new LinkedHashSet<>();
//                for (Map.Entry<LemmaModel, List<IndexModel>> entry : mapLemmaIndexs.entrySet()) {
//                    entry.getValue().forEach(indexModel -> {
//                        if (indexModel.getPageModelId().equals(pageModel)) {
//                            setIndexForPage.add(indexModel);
//                        }
//                    });
//                }
//                float relAbsPage = 0;
//                for (IndexModel index : setIndexForPage) {
//                    relAbsPage = relAbsPage + index.getRank();
//                }
//                outMap.put(pageModel, relAbsPage);
//            }
//
//            Map<PageModel, Float> resMap = outMap.entrySet().stream().sorted(Map.Entry.<PageModel, Float>comparingByValue().reversed()).
//                    collect(Collectors.toMap(
//                    Map.Entry::getKey,
//                    Map.Entry::getValue,
//                    (a, b) -> a,
//                    LinkedHashMap::new
//            ));
//                  //  forEach(System.out::println);
////                    collect(Collectors
////                            .toMap(Map.Entry::getKey,
////                                    Map.Entry::getValue,
////                                    (e1, e2) -> e1,
////                                    LinkedHashMap::new));
//
//
//            List<SearchData> listSearchData = new ArrayList<>();
//                for (int u = 0; u < listObj.size(); u++){
//                    SearchData searchData = new SearchData();
//                    searchData.setSite(listObj.get(u).getPageModel().getSiteModelId().getUrl());
//                    searchData.setSiteName(listObj.get(u).getPageModel().getSiteModelId().getName());
//
//                    String htmlText = listObj.get(u).getPageModel().getContent();
//                    List<IndexModel> indexModels = listObj.get(u).getListIndexModel();
//                    Document doc = Jsoup.parse(htmlText);
//
//
// //                   StringBuilder result = new StringBuilder();
//
//
////                    for (IndexModel im : indexModels){
////                        Elements elementsSnippet = doc.select(im.getLemmaModelId().getLemma());
////                        elementsSnippet.forEach(element -> {
////                            System.out.println("element" + element.text());
////                            result.append(element.text() + "\n");
////                        });
////
////
////                    }
//
//
////                    elementsTitle.forEach(element -> {
////                        System.out.println(element.text());
////                    });
//
//                    searchData.setSnippet(createSnippet(doc, indexModels));
//                    searchData.setTitle(createTitle(doc));
//                    searchData.setUri(listObj.get(u).getPageModel().getPath().replaceAll(searchData.getSite(), ""));
//                    searchData.setRelevance(listObj.get(u).getRelRel());
//                    listSearchData.add(searchData);
//
//                }
//
//            searchResponse.setResult(true);
//            searchResponse.setCount(listObj.size());
//            searchResponse.setData(listSearchData);
//
//
//
//
///*            Map<PageModel, List<IndexModel>> outMap = new LinkedHashMap<>();
//            List<SearchData> listSearchData = new ArrayList<>();
//            for (Map.Entry<PageModel, List<IndexModel>> entry : outMap.entrySet()) {
//
//                SearchData searchData = new SearchData();
//                searchData.setSite(entry.getKey().getSiteModelId().getUrl());
//                searchData.setUri(entry.getKey().getPath().replace(entry.getKey().getSiteModelId().getUrl(), ""));
//                searchData.setSiteName(entry.getKey().getSiteModelId().getName());
//                searchData.setTitle("title");
//                searchData.setSnippet("snippet");
//                searchData.setRelevance(0.999);
//                listSearchData.add(searchData);
//                System.out.println(entry.getKey().getSiteModelId().getUrl() + " - " + entry.getKey().getPath());
//                //Нужно заполнить SearchData и SearchResponse
//            }
//            searchResponse.setResult(true);
//            searchResponse.setCount(outMap.size());
//            searchResponse.setData(listSearchData);*/
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            searchResponse.setResult(false);
//        }
//        return searchResponse;
//    }






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

    public String createSnippet(Document doc, List<IndexModel> listIndexModel) throws IOException {
        StringBuilder result = new StringBuilder();
        List<String> listWords = new ArrayList<>();
        Elements elements = doc.getAllElements();
        //сортируем по Rank потому что нужно в первую очередь выделить слово с наименьшем rank
        listIndexModel.sort(Comparator.comparing(IndexModel::getRank));
        for (IndexModel indexModel : listIndexModel) {
            listWords.add(indexModel.getLemmaModelId().getLemma());
        }
        String answer = searchSegSnip(elements.text().replaceAll("\\s{2,}", " ").trim(), listWords);


        return answer;
    }

//    public String searchSegSnip(String htmlText, List<String> listWords) {
//      //  StringBuilder result = new StringBuilder();
//        int pos = htmlText.indexOf(listWords.get(0));
//        StringBuilder sb = new StringBuilder();
//        String[] tokens = htmlText.split(" ");
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

        public String searchSegSnip(String htmlText, List<String> listWords) {
            StringBuilder result = new StringBuilder();
            System.out.println("listwords size: " + listWords.size());
            for (String word : listWords) {
                System.out.println("RESULT: " + result);
                System.out.println(word);
                if (!result.isEmpty() && (result.indexOf(word) > 0)){
                    System.out.println("REPEAT!!!!!!!!!!!!!!");
                    int indRepStart = result.indexOf(word);
                    int indRepEnd = result.indexOf(" ", indRepStart);

                    result.insert( indRepStart - 1,"<b>");
                    result.insert( indRepEnd + 4,"</b>");
                    continue;
                }


                if (htmlText.contains(word)) {
                    int indStartWord = htmlText.indexOf(word);
                    int indEndWord = htmlText.indexOf(" ", indStartWord);
                    //    int indEndWord = indStartWord + listWords.get(0).length() - 1;
                    System.out.println("Word: " + indStartWord + " - * - " + indEndWord);
                    int indStartPart = (indStartWord - 100) < 0 ? indStartWord - 1 : htmlText.indexOf(" ", indStartWord - 100);
                    int indEndPart = (indEndWord + 100) > htmlText.length() ? indEndWord + 1 : htmlText.indexOf(" ", indEndWord + 70);
                    System.out.println("Part: " + indStartPart + " - * - " + indEndPart);
                    result.append(htmlText.substring(indStartPart, indStartWord - 1) +
                            " <b>" + htmlText.substring(indStartWord, indEndWord) + "</b> " +
                            htmlText.substring(indEndWord + 1, indEndPart) + "\n");
                }
            }



//            int diff = htmlText.length() - indStartWord;
//            if ( diff > 300 ){
//                int indEndTeg = htmlText.indexOf(" ", indStartWord+250);
//            }
//
//            int indEndTeg = htmlText.indexOf(" ", indStartWord);
//            if ((indEndTeg - indStartWord) < 300){
//                if (htmlText.substring(0, indStartWord).length() > 100 ){
//                    int indStartTub = htmlText.indexOf(" ", indStartWord - 100);
//                 result.append(htmlText.substring(indStartTub, indEndTeg));
//                    return result.toString();
//                }else {
//                    if ( htmlText.substring(0, indStartWord).length() > 50 ) {
//                        int indStartTub = htmlText.indexOf(" ", indStartWord - 50);
//                        result.append(htmlText.substring(indStartTub, indEndTeg));
//                        return result.toString();
//                    }else {
//                        result.append(htmlText.substring(indStartWord, indEndTeg));
//                        return result.toString();
//                    }
//
//                }
//            }
//            else {
//                int indEndTub = htmlText.indexOf(" ", indStartWord + 250);
//                result.append(htmlText.substring(indStartWord, indEndTub));
//            }



            return result.toString();
        }


    public String createTitle(Document doc) throws IOException {
        Elements elementsTitle = doc.select("title");
        return elementsTitle.get(0).text();
    }


    public LemmaModel getLeastFrequency(List<LemmaModel> listLemmaModel) {


        return new LemmaModel();
    }



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


