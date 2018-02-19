package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.StackOverFlowDataScraper;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class StackOverFlowDataValdator {

  public static void validate(String language, JSONObject languageData) {
    try {
      DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
      String groupingSeparator = String.valueOf(symbols.getGroupingSeparator());

      String questionCount = languageData.getAsString(StackOverFlowDataScraper.QUESTIONS_COUNT_KEY).replace(groupingSeparator, "");

      ValidatorHelper.setContext(language);
      ValidatorHelper.validateNumber(questionCount, StackOverFlowDataScraper.QUESTIONS_COUNT_KEY);

      StatusLogger.logSuccessFor(language);
    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }
  }
}