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

            DataValidator validator = new DataValidator(language, localRanking);
            validator.validateNumber(MeetupDataScraper.MEETUPS_KEY, 4);
            validator.validateNumber(MeetupDataScraper.MEMBERS_KEY, 3500);


            JSONObject globalRanking = (JSONObject) languageData.get(MeetupDataScraper.GLOBAL_RANKING_KEY);

            validator = new DataValidator(language, globalRanking);
            validator.validateNumber(MeetupDataScraper.MEETUPS_KEY, 150);
            validator.validateNumber(MeetupDataScraper.MEMBERS_KEY, 60000);

            StatusLogger.logSuccessFor(language);
        } catch (Exception e) {
            StatusLogger.logException(language, e);
        }
    }


}