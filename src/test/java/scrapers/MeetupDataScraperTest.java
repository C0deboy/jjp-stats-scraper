package scrapers;

import languageStatistics.StatisticsBuilder;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MeetupDataScraperTest {

  @Test
  public void getData() {
    JSONObject meetupData = new MeetupDataScraper().getData();
    StatisticsBuilder.saveToFile(meetupData, "src/test/statistics/meetup.json");

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    String groupingSeparator = String.valueOf(symbols.getGroupingSeparator());

    for (Map.Entry<String, Object> stats : meetupData.entrySet()) {
      if (MeetupDataScraper.excluded.contains(stats.getKey())) {
        continue;
      }
      JSONObject languageStatsJSON = (JSONObject) stats.getValue();

      JSONObject globalRanking = (JSONObject) languageStatsJSON.get(MeetupDataScraper.GLOBAL_RANKING_KEY);
      String localMeetupsCount = globalRanking.getAsString(MeetupDataScraper.MEETUPS_KEY).replace(groupingSeparator, "");
      String localMembersCount = globalRanking.getAsString(MeetupDataScraper.MEMBERS_KEY).replace(groupingSeparator, "");
      String localPosition = globalRanking.getAsString(MeetupDataScraper.POSITION_KEY);

      JSONObject localRanking = (JSONObject) languageStatsJSON.get(MeetupDataScraper.LOCAL_RANKING_KEY);
      String globalMeetupsCount = localRanking.getAsString(MeetupDataScraper.MEETUPS_KEY).replace(groupingSeparator, "");
      String globalMembersCount = localRanking.getAsString(MeetupDataScraper.MEMBERS_KEY).replace(groupingSeparator, "");
      String globalPosition = localRanking.getAsString(MeetupDataScraper.POSITION_KEY);

      assertNotNull(globalRanking);
      shouldBeNumeric(localMeetupsCount, MeetupDataScraper.MEETUPS_KEY);
      shouldBeNumeric(localMembersCount, MeetupDataScraper.MEMBERS_KEY);
      shouldBeNumeric(localPosition, MeetupDataScraper.POSITION_KEY);

      assertNotNull(localRanking);
      shouldBeNumeric(globalMeetupsCount, MeetupDataScraper.MEETUPS_KEY);
      shouldBeNumeric(globalMembersCount, MeetupDataScraper.MEMBERS_KEY);
      shouldBeNumeric(globalPosition, MeetupDataScraper.POSITION_KEY);
    }
  }

  private void shouldBeNumeric(String localMeetupsCount, String meetupsKey) {
    assertTrue(meetupsKey + " should be numeric.", StringUtils.isNumeric(localMeetupsCount));
  }
}