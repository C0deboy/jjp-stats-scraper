package scrapers;

import mockedScrapers.MockedGithubDataScraper;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GithubDataScraperTest extends BaseScraperTest {

  GithubDataScraperTest() {
    super(new MockedGithubDataScraper(languages));
  }

  @Test
  void validGithubCountsData() {

    assertThat(scraperData).allSatisfy((language, languageStats) -> {
      JSONObject stats = (JSONObject) languageStats;
      String projectsCount = stats.getAsString(GithubDataScraper.PROJECTS_COUNT_KEY).replace(groupingSeparator, "");
      String moreThan1000StarsCount = stats.getAsString(GithubDataScraper.MORE_THEN_1000_STARS_COUNT_KEY).replace(groupingSeparator, "");
      String ranking = stats.getAsString(GithubDataScraper.RANKING_KEY);

      assertThat(projectsCount).satisfies(StringUtils::isNumeric);
      assertThat(moreThan1000StarsCount).satisfies(StringUtils::isNumeric);
      assertThat(ranking).satisfies(StringUtils::isNumeric);
      assertThat(Integer.parseInt(ranking)).as(language + " ranking").isBetween(1, 20);
    });
  }

  @Test
  void validTop10Data() {

    scraperData.values().stream().map(JSONObject.class::cast).forEach(languageStats -> {
      JSONArray top10 = (JSONArray) languageStats.get(GithubDataScraper.TOP10_KEY);

      for (Object projectData : top10) {
        JSONObject projectJSONData = (JSONObject) projectData;
        String projectName = projectJSONData.getAsString(GithubDataScraper.PROJECT_NAME_KEY);
        String projectStars = projectJSONData.getAsString(GithubDataScraper.PROJECT_STARS_COUNT_KEY).replace(groupingSeparator, "");
        String projectUrl = projectJSONData.getAsString(GithubDataScraper.PROJECT_URL_KEY);

        assertThat(projectUrl).satisfies(UrlValidator.getInstance()::isValid);
        assertThat(projectName).satisfies(StringUtils::isNotBlank);
        assertThat(projectStars).satisfies(StringUtils::isNumeric);
      }

      assertThat(top10).hasSize(10);
    });
  }


}