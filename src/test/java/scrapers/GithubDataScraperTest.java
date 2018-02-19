package scrapers;

import languageStatistics.StatisticsBuilder;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.Test;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GithubDataScraperTest {

  @Test
  public void getData() {
    JSONObject githubDataData = new GithubDataScraper().getData();

    StatisticsBuilder.saveToFile(githubDataData, "src/test/statistics/tiobeIndex.json");

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    String groupingSeparator = String.valueOf(symbols.getGroupingSeparator());

    for (Object languageStats : githubDataData.values()) {
      JSONObject languageStatsJSON = (JSONObject) languageStats;

      JSONArray top10 = (JSONArray) languageStatsJSON.get(GithubDataScraper.TOP10_KEY);

      for (Object projectData : top10) {
        JSONObject projectJSONData = (JSONObject) projectData;
        String projectName = projectJSONData.getAsString(GithubDataScraper.PROJECT_NAME_KEY);
        String projectStars = projectJSONData.getAsString(GithubDataScraper.PROJECT_STARS_COUNT_KEY).replace(groupingSeparator, "");
        String projectUrl = projectJSONData.getAsString(GithubDataScraper.PROJECT_URL_KEY);

        assertTrue(GithubDataScraper.PROJECT_URL_KEY + "should be valid URL.", UrlValidator.getInstance().isValid(projectUrl));
        assertTrue(GithubDataScraper.PROJECT_NAME_KEY + "should not be blank.", StringUtils.isNotBlank(projectName));
        assertTrue(GithubDataScraper.PROJECT_STARS_COUNT_KEY + " should be numeric.", StringUtils.isNumeric(projectStars));
      }

      String projectsCount = languageStatsJSON.getAsString(GithubDataScraper.PROJECTS_COUNT_KEY).replace(groupingSeparator, "");
      String moreThan1000StarsCount = languageStatsJSON.getAsString(GithubDataScraper.MORE_THEN_1000_STARS_COUNT_KEY).replace(groupingSeparator, "");
      String ranking = languageStatsJSON.getAsString(GithubDataScraper.RANKING_KEY);

      assertEquals(GithubDataScraper.TOP10_KEY + " should contain 10 projects", 10, top10.size());
      assertTrue(GithubDataScraper.PROJECTS_COUNT_KEY + " should be numeric.", StringUtils.isNumeric(projectsCount));
      assertTrue(GithubDataScraper.MORE_THEN_1000_STARS_COUNT_KEY + " should be numeric.", StringUtils.isNumeric(moreThan1000StarsCount));
      assertTrue(GithubDataScraper.RANKING_KEY + " should be numeric.", StringUtils.isNumeric(ranking));
    }
  }
}