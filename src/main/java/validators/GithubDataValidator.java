package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import scrapers.GithubDataScraper;

public class GithubDataValidator {

    public static void validate(String language, JSONObject languageData) {
        try {
            validateTop10Projects(language, languageData);

            DataValidator validator = new DataValidator(language, languageData);
            validator.validateNumber(GithubDataScraper.PROJECTS_COUNT_KEY, 30000);
            validator.validateNumber(GithubDataScraper.MORE_THEN_1000_STARS_COUNT_KEY, 15);

            StatusLogger.logSuccessFor(language);
        } catch (Exception e) {
            StatusLogger.logException(language, e);
        }
    }

    private static void validateTop10Projects(String language, JSONObject languageData) {
        JSONArray top10 = (JSONArray) languageData.get(GithubDataScraper.TOP10_KEY);

        for (Object projectData : top10) {
            DataValidator validator = new DataValidator(language, (JSONObject) projectData);

            validator.validateUrl(GithubDataScraper.PROJECT_URL_KEY);
            validator.validateNotBlank(GithubDataScraper.PROJECT_NAME_KEY);
            validator.validateNumber(GithubDataScraper.PROJECT_STARS_COUNT_KEY, 1500);
        }

        if (top10.size() != 10) {
            StatusLogger.logErrorFor(language, GithubDataScraper.TOP10_KEY + " does not contain 10 projects");
        }
    }
}