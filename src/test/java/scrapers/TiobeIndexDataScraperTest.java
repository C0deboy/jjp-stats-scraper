package scrapers;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;

class TiobeIndexDataScraperTest extends BaseScraperTest {

  TiobeIndexDataScraperTest() {
    super(new TiobeIndexDataScraper());
  }

  @Test
  void getData() {
    for (Object ranking : scraperData.values()) {
      JSONObject rankingJSON = (JSONObject) ranking;

      String currentPosition = rankingJSON.getAsString(TiobeIndexDataScraper.CURRENT_POSITION_KEY);
      String lastYearPosition = rankingJSON.getAsString(TiobeIndexDataScraper.LAST_YEAR_POSITION_KEY);

      shouldBeNumeric(currentPosition, TiobeIndexDataScraper.LAST_YEAR_POSITION_KEY);
      shouldBeNumeric(lastYearPosition, TiobeIndexDataScraper.CURRENT_POSITION_KEY);

    }
  }
}