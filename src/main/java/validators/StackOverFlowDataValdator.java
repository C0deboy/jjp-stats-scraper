package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.StackOverflowDataScraper;

public class StackOverFlowDataValdator {

    public static void validate(String language, JSONObject languageData) {

        try {
            DataValidator validator = new DataValidator(language, languageData);
            validator.validateNumber(StackOverflowDataScraper.QUESTIONS_COUNT_KEY, 185000);

            StatusLogger.logSuccessFor(language);
        } catch (Exception e) {
            StatusLogger.logException(language, e);
        }
    }
}