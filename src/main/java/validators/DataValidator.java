package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

class DataValidator {
  private static String language = "None";
  private static JSONObject languageData;
  private static String groupingSeparator;

  static void setContext(String language, JSONObject languageData) {
    DataValidator.language = language;
    DataValidator.languageData = languageData;
    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    groupingSeparator = String.valueOf(symbols.getGroupingSeparator());
  }

  static void validateNumber(String dataKey) {
    String actual = languageData.getAsString(dataKey).replace(groupingSeparator, "");

    if (!StringUtils.isNumeric(actual.replace(groupingSeparator, ""))) {
      StatusLogger.logErrorFor(language, dataKey + " is not a number.");
    }
  }

  static void validateNumber(String dataKey, int min, int max) {
    String actual = languageData.getAsString(dataKey).replace(groupingSeparator, "");

    validateNumber(dataKey);

    int actualNumber = Integer.parseInt(actual);
    if (actualNumber < min || actualNumber > max) {
      StatusLogger.logErrorFor(language, dataKey + " is not in range " + min + "-" + max);
    }
  }

  static void validateNumber(String dataKey, int min) {
    String actual = languageData.getAsString(dataKey).replace(groupingSeparator, "");

    validateNumber(dataKey);

    int actualNumber = Integer.parseInt(actual);
    if (actualNumber < min) {
      StatusLogger.logErrorFor(language, dataKey + " is not grater or equal than " + min);
    }
  }

  static void validateNotBlank(String dataKey) {
    String actual = languageData.getAsString(dataKey);
    if (!StringUtils.isNotBlank(actual)) {
      StatusLogger.logErrorFor(language, dataKey + " is empty.");
    }
  }

  static void validateUrl(String dataKey) {
    String actual = languageData.getAsString(dataKey);
    if (!UrlValidator.getInstance().isValid(actual)) {
      StatusLogger.logErrorFor(language, dataKey + " is not valid URL.");
    }
  }
}
