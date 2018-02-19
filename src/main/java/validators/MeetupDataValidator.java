package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.MeetupDataScraper;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MeetupDataValidator {

  public static void validate(String language, JSONObject languageData) {

    if (MeetupDataScraper.excluded.contains(language)) {
      return;
    }

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    String groupingSeparator = String.valueOf(symbols.getGroupingSeparator());

    try {
      JSONObject globalRanking = (JSONObject) languageData.get(MeetupDataScraper.GLOBAL_RANKING_KEY);
      String localMeetupsCount = globalRanking.getAsString(MeetupDataScraper.MEETUPS_KEY).replace(groupingSeparator, "");
      String localMembersCount = globalRanking.getAsString(MeetupDataScraper.MEMBERS_KEY).replace(groupingSeparator, "");

      JSONObject localRanking = (JSONObject) languageData.get(MeetupDataScraper.LOCAL_RANKING_KEY);
      String globalMeetupsCount = localRanking.getAsString(MeetupDataScraper.MEETUPS_KEY).replace(groupingSeparator, "");
      String globalMembersCount = localRanking.getAsString(MeetupDataScraper.MEMBERS_KEY).replace(groupingSeparator, "");

      ValidatorHelper.setContext(language);
      ValidatorHelper.validateNumber(localMeetupsCount, MeetupDataScraper.MEETUPS_KEY);
      ValidatorHelper.validateNumber(localMembersCount, MeetupDataScraper.MEMBERS_KEY);

      ValidatorHelper.validateNumber(globalMeetupsCount, MeetupDataScraper.MEETUPS_KEY);
      ValidatorHelper.validateNumber(globalMembersCount, MeetupDataScraper.MEMBERS_KEY);

      StatusLogger.logSuccessFor(language);
    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }
  }


}