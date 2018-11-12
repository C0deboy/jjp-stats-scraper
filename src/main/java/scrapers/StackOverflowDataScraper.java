package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.jsoup.Jsoup;
import validators.StackOverFlowDataValdator;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StackOverflowDataScraper implements DataScraper {
    public static final String QUESTIONS_COUNT_KEY = "questions";
    public static final String RANKING_KEY = "ranking";
    public static final String NAME = "StackOverFlow";
    public static final String URL = "https://api.stackexchange.com/2.2/tags/{language}/info?site=stackoverflow";

    private Map<String, JSONObject> stackOverFlowData = new ConcurrentHashMap<>();
    private String[] languages;
    private Map<Integer, String> rankingData = new ConcurrentSkipListMap<>();

    public StackOverflowDataScraper(String[] languages) {
        this.languages = languages;
    }

    @Override
    public void scrapData() {
        StatusLogger.logCollecting("Stack OverFlow data");

        stackOverFlowData = Stream.of(languages).parallel()
            .collect(Collectors.toMap(lang -> lang, this::scrap));

        Integer ranking = rankingData.size();
        for (String language : rankingData.values()) {
            stackOverFlowData.get(language).put(RANKING_KEY, ranking--);
        }

    }

    private JSONObject scrap(String language) {
        JSONObject languageData = new JSONObject();

        try {
            String doc = fetchData(language);

            JSONObject data = (JSONObject) JSONValue.parse(doc);
            JSONObject items = (JSONObject) ((JSONArray) data.get("items")).get(0);
            Integer count = (Integer) items.get("count");

            languageData.put(QUESTIONS_COUNT_KEY, String.format("%,d", count));
            rankingData.put(count, language);

        } catch (Exception e) {
            StatusLogger.logException(language, e);
        }

        StackOverFlowDataValdator.validate(language, languageData);

        return languageData;
    }

    protected String fetchData(String language) throws IOException {
        String url = URL.replace("{language}", language.replace("+", "%2B"));
        return Jsoup.connect(url).ignoreContentType(true).execute().body();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public JSONObject getData() {
        return new JSONObject(stackOverFlowData);
    }
}
