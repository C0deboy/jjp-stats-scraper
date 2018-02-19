package scrapers;

import languageStatistics.StatisticsBuilder;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TiobeIndexDataScraperTest {

  @Test
  public void getData() {
    JSONObject tiobeIndexData = new TiobeIndexDataScraper().getData();

    StatisticsBuilder.saveToFile(tiobeIndexData, "src/test/statistics/tiobeIndex.json");

    for (Object ranking : tiobeIndexData.values()) {
      JSONObject rankingJSON = (JSONObject) ranking;

      String currentPosition = rankingJSON.getAsString(TiobeIndexDataScraper.CURRENT_POSITION_KEY);
      assertTrue(TiobeIndexDataScraper.CURRENT_POSITION_KEY + " should be a number.", StringUtils.isNumeric(currentPosition));

      String lastYearPosition = rankingJSON.getAsString(TiobeIndexDataScraper.LAST_YEAR_POSITION_KEY);
      assertTrue(TiobeIndexDataScraper.LAST_YEAR_POSITION_KEY + " should be a number.", StringUtils.isNumeric(lastYearPosition));
    }
  }
}