package scrapers;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MeetupDataScraperTest extends BaseScraperTest {

  MeetupDataScraperTest() {
    super(new MeetupDataScraper());
  }

  @Test
  void getData() {

    for (Map.Entry<String, Object> stats : scraperData.entrySet()) {
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

      assertThat(globalRanking).isNotNull();
      shouldBeNumeric(localMeetupsCount, MeetupDataScraper.MEETUPS_KEY);
      shouldBeNumeric(localMembersCount, MeetupDataScraper.MEMBERS_KEY);
      shouldBeNumeric(localPosition, MeetupDataScraper.POSITION_KEY);

      assertThat(localRanking).isNotNull();
      shouldBeNumeric(globalMeetupsCount, MeetupDataScraper.MEETUPS_KEY);
      shouldBeNumeric(globalMembersCount, MeetupDataScraper.MEMBERS_KEY);
      shouldBeNumeric(globalPosition, MeetupDataScraper.POSITION_KEY);
    }
  }
}