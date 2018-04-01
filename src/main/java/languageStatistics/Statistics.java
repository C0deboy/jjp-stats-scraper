package languageStatistics;

import net.minidev.json.JSONObject;
import scrapers.DataScraper;

public class Statistics {
  private String[] languages;

  public Statistics() {
  }

  public Statistics(String[] languages) {
    this.languages = languages;
  }

  public void collectFor(String[] languages) {
    this.languages = languages;
  }

  public JSONObject build(DataScraper dataScraper) {
    dataScraper.scrapDataFor(languages);
    JSONObject statistics = new JSONObject();
    statistics.put("name", dataScraper.getName());
    statistics.put("data", dataScraper.getData());
    return statistics;
  }

}
