package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.SpectrumDataScraper;

public class SpectrumDataValidator {

    public static void validate(String language, JSONObject languageData) {

        try {
            DataValidator validator = new DataValidator(language, languageData);
            validator.validateNumber(SpectrumDataScraper.CURRENT_POSITION_KEY, 1, 20);
            validator.validateNumber(SpectrumDataScraper.LAST_YEAR_POSITION_KEY, 1, 20);

            StatusLogger.logSuccessFor(language);
        } catch (Exception e) {
            StatusLogger.logException(language, e);
        }
    }
}