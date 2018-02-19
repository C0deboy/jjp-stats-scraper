package scrapers;

import languageStatistics.StatisticsBuilder;
import net.minidev.json.JSONObject;

public interface DataScraper {
  String[] languages = StatisticsBuilder.languages;

  String getName();

  JSONObject getData();

}
