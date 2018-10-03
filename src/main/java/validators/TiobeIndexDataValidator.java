package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.TiobeIndexDataScraper;

public class TiobeIndexDataValidator {

    public static void validate(String language, JSONObject languageData) {

        try {
            DataValidator validator = new DataValidator(language, languageData);
            validator.validateNumber(TiobeIndexDataScraper.CURRENT_POSITION_KEY, 1, 25);
            validator.validateNumber(TiobeIndexDataScraper.LAST_YEAR_POSITION_KEY, 1, 25);

            StatusLogger.logSuccessFor(language);
        } catch (Exception e) {
            StatusLogger.logException(language, e);
        }
    }
}