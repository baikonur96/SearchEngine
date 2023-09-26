package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.search.SearchResponse;
import searchengine.model.LemmaModel;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexModelRepository;
import searchengine.repositories.LemmaModelRepository;
import searchengine.repositories.PageModelRepository;
import searchengine.repositories.SiteModelRepository;

import java.io.IOException;
import java.util.*;


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
            SiteModel searchSite = siteModelRepository.findByUrl(site);
            Map<String, Integer> lemmas = LemmaFinder.getInstance().collectLemmas(query);
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                List<LemmaModel> listLemmaModelInDb = new ArrayList<>();
                if (site == null) {
                    List<LemmaModel> listRepLemma = lemmaModelRepository.findAllByLemma(entry.getKey()); // Надо передать методу который разберёт лист и передаст объект с наименьшим frequency
                } else {
                    listLemmaModelInDb.add(lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), searchSite));

                   // res = indexModelRepository.findAllByLemmaModelByLemmaIdAndSiteId(lemT, searchSite);
                }


                lemmaModelRepository.findByLemmaAndSiteModelId(entry.getKey(), );


            }

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
        } catch (Exception e) {
            e.printStackTrace();
            searchResponse.setResult(false);
        }
        return searchResponse;
    }


        public LemmaModel getLeastFrequency(List<LemmaModel> listLemmaModel) {


        return new LemmaModel();
        }
/*    public String getAt(String st, int pos) {
        StringBuilder sb = new StringBuilder();
        String[] tokens = st.split(" ");

        int pre = 10;
        int post = 10;
        if (pos < pre) {
            pre = pos;
            post = post + 10 - pos;
        }
        if (pos > tokens.length - post) {
            post = tokens.length - post - 1;
            pre = pre + (10 - post);
        }

        for (int i = pos - pre; i < pos + post; i++) {
            if (lemmaFinder.collectLemmas(tokens[i]).size() > 0 &&
                    queryLemmas.contains(lemmaFinder.collectLemmas(tokens[i]).keySet().stream().findFirst().orElse(""))) {
                sb.append("<b>").append(tokens[i]).append("</b>");
            } else {
                sb.append(tokens[i]);
            }
            sb.append(" ");
        }

        return sb.toString();
    }*/
}
