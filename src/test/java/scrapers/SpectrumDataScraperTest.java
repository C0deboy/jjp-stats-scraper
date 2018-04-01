package scrapers;

import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpectrumDataScraperTest extends BaseScraperTest {

  SpectrumDataScraperTest() {
    super(new SpectrumDataScraper());
  }

  @Test
  void currentPositionShouldBeValidNumber() {
    assertThat(scraperData).allSatisfy((language, languageStats) -> {
      JSONObject stats = (JSONObject) languageStats;

      String currentPosition = stats.getAsString(SpectrumDataScraper.LAST_YEAR_POSITION_KEY);

      assertThat(StringUtils.isNumeric(currentPosition)).as(language + " ranking").isTrue();
      assertThat(Integer.parseInt(currentPosition)).as(language + " ranking").isBetween(1, 20);
    });
  }

  @Test
  void lastYearPositionShouldBeValidNumber() {
    scraperData.forEach((language, languageStats) -> {
      JSONObject stats = (JSONObject) languageStats;

      String currentPosition = stats.getAsString(SpectrumDataScraper.LAST_YEAR_POSITION_KEY);

      assertThat(currentPosition).as(language + " currentPosition").satisfies(StringUtils::isNumeric);

      assertThat(Integer.parseInt(currentPosition)).as(language + " currentPosition").isBetween(1, 20);
    });
  }

}