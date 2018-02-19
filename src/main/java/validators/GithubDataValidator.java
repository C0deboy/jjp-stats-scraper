package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import scrapers.GithubDataScraper;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class GithubDataValidator {
  public static void validate(String language, JSONObject languageData) {

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    String groupingSeparator = String.valueOf(symbols.getGroupingSeparator());

    try {
      JSONArray top10 = (JSONArray) languageData.get(GithubDataScraper.TOP10_KEY);

      ValidatorHelper.setContext(language);

      for (Object projectData : top10) {
        JSONObject projectJSONData = (JSONObject) projectData;
        String projectName = projectJSONData.getAsString(GithubDataScraper.PROJECT_NAME_KEY);
        String projectStars = projectJSONData.getAsString(GithubDataScraper.PROJECT_STARS_COUNT_KEY).replace(groupingSeparator, "");
        String projectUrl = projectJSONData.getAsString(GithubDataScraper.PROJECT_URL_KEY);

        ValidatorHelper.validateUrl(projectUrl, GithubDataScraper.PROJECT_URL_KEY);
        ValidatorHelper.validateNotBlank(projectName, GithubDataScraper.PROJECT_NAME_KEY);
        ValidatorHelper.validateNumber(projectStars, GithubDataScraper.PROJECT_STARS_COUNT_KEY);
      }

      String projectsCount = languageData.getAsString(GithubDataScraper.PROJECTS_COUNT_KEY).replace(groupingSeparator, "");
      String moreThan1000StarsCount = languageData.getAsString(GithubDataScraper.MORE_THEN_1000_STARS_COUNT_KEY).replace(groupingSeparator, "");

      if (top10.size() != 10) {
        StatusLogger.logErrorFor(language, GithubDataScraper.TOP10_KEY + " does not contain 10 projects");
      }

      ValidatorHelper.validateNumber(projectsCount, GithubDataScraper.PROJECTS_COUNT_KEY);
      ValidatorHelper.validateNumber(moreThan1000StarsCount, GithubDataScraper.MORE_THEN_1000_STARS_COUNT_KEY);

      StatusLogger.logSuccessFor(language);
    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }
  }
}