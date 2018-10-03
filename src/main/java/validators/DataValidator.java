package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

class DataValidator {
    private String language = "None";
    private JSONObject languageData;
    private String groupingSeparator;

    DataValidator(String language, JSONObject languageData) {
        this.language = language;
        this.languageData = languageData;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        this.groupingSeparator = String.valueOf(symbols.getGroupingSeparator());
    }

    public void validateNumber(String dataKey) {
        String actual = languageData.getAsString(dataKey).replace(groupingSeparator, "");

        if (!StringUtils.isNumeric(actual.replace(groupingSeparator, ""))) {
            StatusLogger.logErrorFor(language, dataKey + " is not a number.");
        }
    }

    public void validateNumber(String dataKey, int min, int max) {
        String actual = languageData.getAsString(dataKey).replace(groupingSeparator, "");

        validateNumber(dataKey);

        int actualNumber = Integer.parseInt(actual);
        if (actualNumber < min || actualNumber > max) {
            StatusLogger.logErrorFor(language, dataKey + " is not in range " + min + "-" + max);
        }
    }

    public void validateNumber(String dataKey, int min) {
        String actual = languageData.getAsString(dataKey).replace(groupingSeparator, "");

        validateNumber(dataKey);

        int actualNumber = Integer.parseInt(actual);
        if (actualNumber < min) {
            StatusLogger.logErrorFor(language, dataKey + " is not grater or equal than " + min);
        }
    }

    public void validateNotBlank(String dataKey) {
        String actual = languageData.getAsString(dataKey);
        if (!StringUtils.isNotBlank(actual)) {
            StatusLogger.logErrorFor(language, dataKey + " is empty.");
        }
    }

    public void validateUrl(String dataKey) {
        String actual = languageData.getAsString(dataKey);
        if (!UrlValidator.getInstance().isValid(actual)) {
            StatusLogger.logErrorFor(language, dataKey + " is not valid URL.");
        }
    }
}
