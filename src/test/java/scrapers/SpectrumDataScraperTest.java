package scrapers;

import languageStatistics.StatisticsBuilder;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SpectrumDataScraperTest {

  @Test
  public void getData() {
    JSONObject spectrumData = new SpectrumDataScraper().getData();
    StatisticsBuilder.saveToFile(spectrumData, "src/test/statistics/spectrum.json");

    for (Object languageStats : spectrumData.values()) {
      JSONObject languageStatsJSON = (JSONObject) languageStats;
      String currentPosition = languageStatsJSON.getAsString(SpectrumDataScraper.CURRENT_POSITION_KEY);
      String lastYearPosition = languageStatsJSON.getAsString(SpectrumDataScraper.LAST_YEAR_POSITION_KEY);

      assertTrue(StackOverFlowDataScraper.QUESTIONS_COUNT_KEY + " should be numeric.", StringUtils.isNumeric(currentPosition));
      assertTrue(StackOverFlowDataScraper.RANKING_KEY + " should be numeric.", StringUtils.isNumeric(lastYearPosition));
    }
  }
}