package searchengine.dto.search;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class Snippet {
    private final StringBuilder value;
    private int beginIndex;
    private int endIndex;
    private final Set<String> lemmas;
    private int totalLemmaCount;
}
