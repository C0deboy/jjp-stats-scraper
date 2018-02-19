package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.SpectrumDataScraper;

public class SpectrumDataValidator {

  public static void validate(String language, JSONObject languageData) {

    try {
      String currentPosition = languageData.getAsString(SpectrumDataScraper.CURRENT_POSITION_KEY);
      String lastYearPosition = languageData.getAsString(SpectrumDataScraper.LAST_YEAR_POSITION_KEY);

      ValidatorHelper.setContext(language);
      ValidatorHelper.validateNumber(currentPosition, SpectrumDataScraper.CURRENT_POSITION_KEY);
      ValidatorHelper.validateNumber(lastYearPosition, SpectrumDataScraper.LAST_YEAR_POSITION_KEY);

      StatusLogger.logSuccessFor(language);
    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }
  }
}