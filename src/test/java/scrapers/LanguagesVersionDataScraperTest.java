package scrapers;

import languageStatistics.StatisticsBuilder;
import net.minidev.json.JSONObject;
import org.junit.Test;

public class LanguagesVersionDataScraperTest {

  @Test
  public void getData() {
    JSONObject languagesVersions = new LanguagesVersionDataScraper().getData();

    StatisticsBuilder.saveToFile(languagesVersions, "src/test/statistics/languagesVersion.json");
  }
}