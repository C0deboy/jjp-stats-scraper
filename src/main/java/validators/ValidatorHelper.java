package validators;

import languageStatistics.StatusLogger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

class ValidatorHelper {
  private static String context = "None";

  public static void setContext(String language) {
    ValidatorHelper.context = language;
  }

  public static void validateNumber(String expectedNumber, String key) {
    if (!StringUtils.isNumeric(expectedNumber)) {
      StatusLogger.logErrorFor(context, key + " is not a number.");
    }
  }

  public static void validateNotBlank(String expectedNotNull, String key) {
    if (!StringUtils.isNotBlank(expectedNotNull)) {
      StatusLogger.logErrorFor(context, key + " is empty.");
    }
  }

  public static void validateUrl(String projectUrl, String key) {
    if (!UrlValidator.getInstance().isValid(projectUrl)) {
      StatusLogger.logErrorFor(context, key + " is not valid URL.");
    }
  }
}
