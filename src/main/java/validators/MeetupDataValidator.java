package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import scrapers.MeetupDataScraper;

public class MeetupDataValidator {

  public static void validate(String language, JSONObject languageData) {

    if (MeetupDataScraper.excluded.contains(language)) {
      return;
    }

    try {
      JSONObject localRanking = (JSONObject) languageData.get(MeetupDataScraper.LOCAL_RANKING_KEY);

      DataValidator.setContext(language, localRanking);
      DataValidator.validateNumber(MeetupDataScraper.MEETUPS_KEY, 10);
      DataValidator.validateNumber(MeetupDataScraper.MEMBERS_KEY, 3500);


      JSONObject globalRanking = (JSONObject) languageData.get(MeetupDataScraper.GLOBAL_RANKING_KEY);

      DataValidator.setContext(language, globalRanking);
      DataValidator.validateNumber(MeetupDataScraper.MEETUPS_KEY, 200);
      DataValidator.validateNumber(MeetupDataScraper.MEMBERS_KEY, 75000);

      StatusLogger.logSuccessFor(language);
    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }
  }


}