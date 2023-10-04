package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchData;
import searchengine.dto.search.SearchResponse;
import searchengine.model.IndexModel;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexModelRepository;
import searchengine.repositories.LemmaModelRepository;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;

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
    public SearchResponse search(String query, String site, Integer offset, Integer limit) {
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
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                //Сайт не указан
                // Надо передать методу который разберёт лист и передаст объект с наименьшим frequency
                // Если нет сайта
                System.out.println(entry.getKey());
                if (site == null && lemmaModelRepository.existsByLemma(entry.getKey())) {
                    List<LemmaModel> listRepLemma = lemmaModelRepository.findAllByLemma(entry.getKey());
                    listRepLemma.sort(Comparator.comparing(LemmaModel::getFrequency));
/*                    for (int z = 0; z < listRepLemma.size(); z++){
                        System.out.println(listRepLemma.get(z).getLemma() + " - " + listRepLemma.get(z).getFrequency());
                    }*/
                    listLemmaModelInDb.add(listRepLemma.get(0));
                } else if (lemmaModelRepository.existsByLemmaAndSiteModelId(entry.getKey(), searchSite)) {
                    System.out.println("----------------------");
                    LemmaModel lemmaModel = lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), searchSite);
                    listLemmaModelInDb.add(lemmaModel);
                } else {
                    continue;
                }

            }
            if (listLemmaModelInDb.isEmpty()) {
                searchResponse.setResult(false);
                return searchResponse;
            }
            for (int z = 0; z < listLemmaModelInDb.size(); z++) {
                System.out.println(listLemmaModelInDb.get(z).getLemma() + " - " + listLemmaModelInDb.get(z).getFrequency());
            }
            listLemmaModelInDb.sort(Comparator.comparing(LemmaModel::getFrequency));
            for (int z = 0; z < listLemmaModelInDb.size(); z++) {
                System.out.println(listLemmaModelInDb.get(z).getLemma() + " - " + listLemmaModelInDb.get(z).getFrequency());
            }

//Делаем ответ без привязки к сайту
//            for (int i = 1; i < listLemmaModelInDb.size(); i++) {
//                listIndexModelInDb.addAll(indexModelRepository.findAllByLemmaModelId(listLemmaModelInDb.get(0)));
//
//            }

            listIndexModelInDb.addAll(indexModelRepository.findAllByLemmaModelId(listLemmaModelInDb.get(0)));
//Берём первую самую редку лемму
            listIndexModelInDb.forEach(indexModel -> {
                setPageModelinDB.add(indexModel.getPageModelId());
            });


            for (int i = 0; i < listLemmaModelInDb.size(); i++) {
                List<PageModel> removeList = new ArrayList<>();
                for (PageModel model : setPageModelinDB) {
                    List<IndexModel> otherList = indexModelRepository.findAllByLemmaModelIdAndPageModelId(listLemmaModelInDb.get(i), model);
                    if (otherList.isEmpty()) {
//                            if (outMap.containsKey(model)){
//                                outMap.remove(model);
//                            }
                        removeList.add(model);
                    } else {
                        if (mapLemmaIndexs.containsKey(listLemmaModelInDb.get(i)))
                            mapLemmaIndexs.get(listLemmaModelInDb.get(i)).addAll(otherList);
                        else {
                            mapLemmaIndexs.put(listLemmaModelInDb.get(i), otherList);
                        }
                    }
                }
                setPageModelinDB.removeAll(removeList);
            }



//            setPageModelinDB.forEach(pageModel -> {
//                System.out.println(pageModel.getPath() + " -  Нужные сайты");
//            });

//            for (Map.Entry<LemmaModel, List<IndexModel>> entry : mapLemmaIndexs.entrySet()) {
//                System.out.println(entry.getKey().getLemma() + " - " + entry.getValue().get(0).getPageModelId().getPath());
//            }


            //Берём первую самую редку лемму
//            listIndexModelInDb.forEach(indexModel -> {
//                setPageModelinDB.add(indexModel.getPageModelId());
//            });

/*            for (int i = 0; i <setPageModelinDB.size(); i++){


            }*/
            //Добавляем остальные
//            listIndexModelInDb.clear();
//            for (int i = 1; i < listLemmaModelInDb.size(); i++) {
//                List<IndexModel> otherListIndexModel = indexModelRepository.findAllByLemmaModelId(listLemmaModelInDb.get(i));
//                listIndexModelInDb.addAll(otherListIndexModel);
//            }
//
//            for (int j = 0; j < listIndexModelInDb.size(); j++) {
//                for (PageModel pageModel : setPageModelinDB) {
//                    if (!pageModel.equals(listIndexModelInDb.get(j).getPageModelId())) {
//                        setPageModelinDB.remove(pageModel);
//                    }
//                }
//            }

/*           Map<PageModel, List<IndexModel>> outMap = new LinkedHashMap<>();

            //Set<PageModel> pageModelSet = new LinkedHashSet<>();
            for (int y = 0; y < listIndexModelInDb.size(); y++) {

                Optional<PageModel> pageModel = pageModelRepository.findById(listIndexModelInDb.get(y).getPageModelId().getId());

                if (outMap.containsKey(pageModel)) {
                    outMap.get(pageModel).add(listIndexModelInDb.get(y));
                } else {
                    List<IndexModel> listForMap = new ArrayList<>();
                    listForMap.add(listIndexModelInDb.get(y));
                    outMap.put(pageModel.get(), listForMap);
                }
                // pageModelSet.add(pageModelRepository.findById(setPageModelinDB.get(y)));
            }*/


            List<SearchData> listSearchData = new ArrayList<>();
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

            outMap.entrySet().stream().sorted(Map.Entry.<PageModel, Float>comparingByValue().reversed()).forEach(System.out::println);
//                    collect(Collectors
//                            .toMap(Map.Entry::getKey,
//                                    Map.Entry::getValue,
//                                    (e1, e2) -> e1,
//                                    LinkedHashMap::new));
            for (Map.Entry<PageModel, Float> entry : outMap.entrySet()) {
                System.out.println(entry.getKey().getPath() + " - " + entry.getValue());
                SearchData searchData = new SearchData();
                searchData.setSite(entry.getKey().getSiteModelId().getUrl());
                searchData.setUri(entry.getKey().getPath().replace(entry.getKey().getSiteModelId().getUrl(), ""));
                searchData.setSiteName(entry.getKey().getSiteModelId().getName());
                searchData.setTitle(entry.getKey().getPath());
                searchData.setSnippet("snippet");
                searchData.setRelevance(entry.getValue());
                listSearchData.add(searchData);

            }
            searchResponse.setResult(true);
            searchResponse.setCount(setPageModelinDB.size());
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

    public double mathRelevan(Set<IndexModel> indexModelSet) {

        for (IndexModel indexModel : indexModelSet){
        }




        return 0.0;
    }


    //  lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), );


//            lemmas.keySet().forEach(lem -> {
//                queryLemmas.add(lem)
//                SearchClassList searchClassList = new SearchClassList(lem.getLemma());
//                lemmaTRepository.findAllByLemma(lem.getLemma())
//                        .stream()
//                        .map(lemT -> {
//                            List<IndexModel> res;
//                            if (site == null) {
//                                res = indexTRepository.findAllByLemmaTByLemmaId(lemT);
//                            } else {
//                                res = indexTRepository.findAllByLemmaTByLemmaIdAndSiteId(lemT, searchSite);
//                            }
//                            return res;
//                        })
//                        .forEach(searchClassList::addList);
//                searchClassAllPages.add(searchClassList);
//            });
//            if (searchClassAllPages.size() >= 1) {
//                searchClassAllPages.intersectAll();
//            }
//            if (searchClassAllPages.size() == 0) {
//                throw new IOException("bad request");
//            }
//            //
//            //printing the sorted hashmap
//            Set<Map.Entry<Integer, PageRel>> set = searchClassAllPages.getSortedMap().entrySet();
//
//            int cnt = 0;
//            for (Map.Entry<Integer, PageRel> me2 : set) {
//                cnt++;
//                if (cnt <= offset) continue;
//                if (cnt > limit + offset) break;
//                PageT pageT = pageTRepository.findByPageId(me2.getKey());
//                String text = pageT.getContent().replaceAll("\\s{2,}", " ").trim();
//                Map<Integer, String> textLemList = lemmaFinder.collectLemmasList(text);
//                String word = lemmaMap.keySet().stream().findAny().orElseThrow().getLemma();
//                int pos = textLemList.entrySet()
//                        .stream()
//                        .filter(e -> e.getValue().toLowerCase(Locale.ROOT).equals(word.toLowerCase(Locale.ROOT)))
//                        .map(Map.Entry::getKey)
//                        .findFirst()
//                        .orElse(0);
//                String snippet = "..." + getAt(text, pos) + "...";
//                SiteT siteT = siteTRepository.findByPageId(pageT.getPageId());
//                SearchData searchData = new SearchData(siteT.getUrl(), siteT.getName(), pageT.getPath(), pageT.getTitle(), snippet, searchClassAllPages.getMapRank().get(me2.getKey()).getRelRank());
//                searchResponse.dataAdd(searchData);
//            }
//            logger.log(Level.forName("DIAG", 350), "query = \t" + query);
//            searchResponse.setResult(true);
//            searchResponse.setCount(set.size());


    public LemmaModel getLeastFrequency(List<LemmaModel> listLemmaModel) {


        return new LemmaModel();
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

}
