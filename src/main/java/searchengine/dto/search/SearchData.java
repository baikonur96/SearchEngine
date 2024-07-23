package searchengine.dto.search;

import jdk.jshell.Snippet;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
public class SearchData {
    private String site;
    private String siteName;
    private String uri;
    private  String title;
    private Snippet snippet;
    private double relevance;


}
