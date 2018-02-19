package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.jsoup.Jsoup;
import validators.StackOverFlowDataValdator;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class StackOverFlowDataScraper implements DataScraper {
  public static final String QUESTIONS_COUNT_KEY = "questions";
  public static final String RANKING_KEY = "ranking";
  private final String NAME = "StackOverFlow";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public JSONObject getData() {
    StatusLogger.logCollecting("Stack OverFlow data");

    Map<String, JSONObject> stackOverFlowData = new HashMap<>();
    Map<Integer, String> rankingData = new TreeMap<>();

    for (String language : languages) {
      String url = "https://api.stackexchange.com/2.2/tags/" + language.replace("+", "%2B") + "/info?site=stackoverflow";
      JSONObject languageData = new JSONObject();

      try {
        String doc = Jsoup.connect(url).ignoreContentType(true).execute().body();

        JSONObject data = (JSONObject) JSONValue.parse(doc);
        JSONObject items = (JSONObject) ((JSONArray) data.get("items")).get(0);
        Integer count = (Integer) items.get("count");

        languageData.put(QUESTIONS_COUNT_KEY, String.format("%,d", count));
        rankingData.put(count, language);

      } catch (Exception e) {
        StatusLogger.logException(language, e);
      }

      StackOverFlowDataValdator.validate(language, languageData);
      stackOverFlowData.put(language, languageData);
    }

    Integer ranking = rankingData.size();
    for (String language : rankingData.values()) {
      stackOverFlowData.get(language).put(RANKING_KEY, ranking--);
    }

    return new JSONObject(stackOverFlowData);
  }
}
