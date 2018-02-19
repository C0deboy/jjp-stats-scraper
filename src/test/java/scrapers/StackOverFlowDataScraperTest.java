package scrapers;

import languageStatistics.StatisticsBuilder;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.assertTrue;

public class StackOverFlowDataScraperTest {

  @Test
  public void getData() {

    JSONObject stackOverFlowData = new StackOverFlowDataScraper().getData();
    StatisticsBuilder.saveToFile(stackOverFlowData, "src/test/statistics/stackOverFlow.json");

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    String groupingSeparator = String.valueOf(symbols.getGroupingSeparator());

    for (Object languageStats : stackOverFlowData.values()) {
      JSONObject languageStatsJSON = (JSONObject) languageStats;
      String questionCount = languageStatsJSON.getAsString(StackOverFlowDataScraper.QUESTIONS_COUNT_KEY).replace(groupingSeparator, "");
      String ranking = languageStatsJSON.getAsString(StackOverFlowDataScraper.RANKING_KEY).replace(groupingSeparator, "");

      assertTrue(StackOverFlowDataScraper.QUESTIONS_COUNT_KEY + " should be numeric.", StringUtils.isNumeric(questionCount));
      assertTrue(StackOverFlowDataScraper.RANKING_KEY + " should be numeric.", StringUtils.isNumeric(ranking));
    }
  }
}