package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.TiobeIndexDataScraper;

public class TiobeIndexDataValidator {

  public static void validate(String language, JSONObject languageData) {
    try {
      String currentPosition = languageData.getAsString(TiobeIndexDataScraper.CURRENT_POSITION_KEY);
      String lastYearPosition = languageData.getAsString(TiobeIndexDataScraper.LAST_YEAR_POSITION_KEY);

      ValidatorHelper.setContext(language);
      ValidatorHelper.validateNumber(currentPosition, TiobeIndexDataScraper.CURRENT_POSITION_KEY);
      ValidatorHelper.validateNumber(lastYearPosition, TiobeIndexDataScraper.LAST_YEAR_POSITION_KEY);

      StatusLogger.logSuccessFor(language);
    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }
  }
}