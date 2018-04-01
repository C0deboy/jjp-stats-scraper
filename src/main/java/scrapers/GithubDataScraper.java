package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.jsoup.Jsoup;
import validators.GithubDataValidator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class GithubDataScraper implements DataScraper {
  public static final String TOP10_KEY = "top10";
  public static final String PROJECTS_COUNT_KEY = "projects";
  public static final String PROJECT_NAME_KEY = "name";
  public static final String PROJECT_STARS_COUNT_KEY = "stars";
  public static final String PROJECT_URL_KEY = "url";
  public static final String RANKING_KEY = "ranking";
  public static final String MORE_THEN_1000_STARS_COUNT_KEY = "moreThen1000Stars";
  public static final String NAME = "Github";
  private Map<String, JSONObject> githubData = new HashMap<>();
  private final Properties properties;

  public GithubDataScraper() {
    this.properties = new Properties();
    try {
      properties.load(GithubDataScraper.class.getClassLoader().getResourceAsStream("config.properties"));
    } catch (IOException e) {
      StatusLogger.logException("Cannot load properties", e);
    }
  }

  @Override
  public void scrapDataFor(String[] languages) {
    StatusLogger.logCollecting("Github data");

    Map<Integer, String> rankingData = new TreeMap<>();

    String authToken = properties.getProperty("GithubAuthToken");

    for (String language : languages) {
      String escapedLanguage = language.replace("+", "%2B");
      String url = "https://api.github.com/search/repositories?q=language:" + escapedLanguage + "+stars:>0&s=stars&per_page=10";
      String urlStars = "https://api.github.com/search/repositories?q=language:" + escapedLanguage + "+stars:>1000&per_page=1";

      JSONObject languageData = new JSONObject();

      try {
        getProjectsData(rankingData, authToken, language, url, languageData);
        getMoreThan1000StarsData(authToken, languageData, urlStars);

      } catch (Exception e) {
        checkGithubApiLimits(authToken);
        StatusLogger.logException(language, e);
      }

      GithubDataValidator.validate(language, languageData);
      githubData.put(language, languageData);
    }

    int ranking = rankingData.size();
    for (String language : rankingData.values()) {
      githubData.get(language).put(RANKING_KEY, ranking--);
    }
  }

  private void getProjectsData(Map<Integer, String> rankingData, String authToken, String language, String url, JSONObject languageData) throws IOException {
    String doc = Jsoup.connect(url).header("Authorization", authToken).ignoreContentType(true).execute().body();

    JSONObject data = (JSONObject) JSONValue.parse(doc);
    JSONArray top10List = (JSONArray) data.get("items");
    JSONArray top10Data = new JSONArray();

    for (Object project : top10List) {
      JSONObject projectJSON = (JSONObject) project;

      JSONObject projectData = new JSONObject();

      projectData.put(PROJECT_NAME_KEY, projectJSON.get("name"));
      projectData.put(PROJECT_STARS_COUNT_KEY, String.format("%,d", (Integer) projectJSON.get("watchers_count")));
      projectData.put(PROJECT_URL_KEY, projectJSON.get("html_url"));

      top10Data.add(projectData);
    }

    Integer count = (Integer) data.get("total_count");
    languageData.put(TOP10_KEY, top10Data);
    languageData.put(PROJECTS_COUNT_KEY, String.format("%,d", count));
    rankingData.put(count, language);
  }

  private void getMoreThan1000StarsData(String authToken, JSONObject languageData, String urlStars) throws IOException {
    String doc = Jsoup.connect(urlStars).header("Authorization", authToken).ignoreContentType(true).execute().body();
    JSONObject data = (JSONObject) JSONValue.parse(doc);
    languageData.put(MORE_THEN_1000_STARS_COUNT_KEY, String.format("%,d", (Integer) data.get("total_count")));
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public JSONObject getData() {
    return new JSONObject(githubData);
  }

  public void checkGithubApiLimits(String authToken) {
    String url = "https://api.github.com/rate_limit";

    try {
      String doc = Jsoup.connect(url).header("Authorization", authToken).ignoreContentType(true).execute().body();
      StatusLogger.logInfo(doc);
    } catch (IOException e) {
      StatusLogger.logException("Checking api limits", e);
    }
  }
}
