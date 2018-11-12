package scrapers;

import mockedScrapers.MockedGithubDataScraper;
import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class StackOverflowDataScraperTest extends BaseScraperTest {

  StackOverflowDataScraperTest() {
    super(new MockedGithubDataScraper(languages));
  }

  @Test
  void getData() {

    for (Object languageStats : scraperData.values()) {
      JSONObject languageStatsJSON = (JSONObject) languageStats;
      String questionCount = languageStatsJSON.getAsString(StackOverflowDataScraper.QUESTIONS_COUNT_KEY).replace(groupingSeparator, "");
      String ranking = languageStatsJSON.getAsString(StackOverflowDataScraper.RANKING_KEY).replace(groupingSeparator, "");

      shouldBeNumeric(questionCount, StackOverflowDataScraper.QUESTIONS_COUNT_KEY);
      shouldBeNumeric(ranking, StackOverflowDataScraper.RANKING_KEY);
      assertThat(Integer.parseInt(ranking)).as(StackOverflowDataScraper.RANKING_KEY).isBetween(1, 20);
    }
  }
}