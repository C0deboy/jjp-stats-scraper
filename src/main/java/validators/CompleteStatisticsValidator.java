package validators;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import scrapers.DataScraper;
import scrapers.GithubDataScraper;
import scrapers.MeetupDataScraper;
import scrapers.SpectrumDataScraper;
import scrapers.StackOverflowDataScraper;

import java.util.Set;

public class CompleteStatisticsValidator {

  public static void validate(JSONObject mergedStatistics, String[] languages, Set<DataScraper> scrapers) {
    if (mergedStatistics.size() != languages.length + 1) {
      StatusLogger.logError("Missing language or data.");
    }
    for (String language : mergedStatistics.keySet()) {

      if (language.equals("date")) {
        continue;
      }

      if (!ArrayUtils.contains(languages, language.replace("pp", "++"))) {
        StatusLogger.logErrorFor(language, language + " is not in given languages.");
      }

      JSONObject languageData = (JSONObject) mergedStatistics.get(language);

      if (languageData.size() != scrapers.size()) {
        StatusLogger.logErrorFor(language, language + " doesn't have complete data.");
      }

      try {
        JSONObject spectrumData = (JSONObject) languageData.get(SpectrumDataScraper.NAME);

        if (scrapers.stream().anyMatch(GithubDataScraper.class::isInstance)) {
          JSONObject githubData = (JSONObject) languageData.get(GithubDataScraper.NAME);
          DataValidator.setContext(language, githubData);
          DataValidator.validateNumber(GithubDataScraper.RANKING_KEY, 1, 10);
        }

        if (scrapers.stream().anyMatch(StackOverflowDataScraper.class::isInstance)) {
          JSONObject stackOverflowData = (JSONObject) languageData.get(StackOverflowDataScraper.NAME);
          DataValidator.setContext(language, stackOverflowData);
          DataValidator.validateNumber(StackOverflowDataScraper.RANKING_KEY, 1, 10);
        }

        if (scrapers.stream().anyMatch(MeetupDataScraper.class::isInstance) && !MeetupDataScraper.excluded.contains(language)) {
          JSONObject meetupData = (JSONObject) languageData.get(MeetupDataScraper.NAME);

          DataValidator.setContext(language, (JSONObject) meetupData.get(MeetupDataScraper.GLOBAL_RANKING_KEY));
          DataValidator.validateNumber(MeetupDataScraper.POSITION_KEY, 1, 10);

          DataValidator.setContext(language, (JSONObject) meetupData.get(MeetupDataScraper.LOCAL_RANKING_KEY));
          DataValidator.validateNumber(MeetupDataScraper.POSITION_KEY, 1, 10);
        }

      } catch (Exception e) {
        StatusLogger.logException("Not complete data.", e);
      }
    }
  }
}
