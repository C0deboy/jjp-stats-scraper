package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.SpectrumDataScraper;

public class SpectrumDataValidator {

  public static void validate(String language, JSONObject languageData) {

    try {
      DataValidator.setContext(language, languageData);
      DataValidator.validateNumber(SpectrumDataScraper.CURRENT_POSITION_KEY, 1, 20);
      DataValidator.validateNumber(SpectrumDataScraper.LAST_YEAR_POSITION_KEY, 1, 20);

      StatusLogger.logSuccessFor(language);
    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }
  }
}