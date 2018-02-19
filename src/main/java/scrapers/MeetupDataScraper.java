package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import validators.MeetupDataValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MeetupDataScraper implements DataScraper {
  public static final String MEETUPS_KEY = "meetups";
  public static final String MEMBERS_KEY = "members";
  public static final String LOCAL_RANKING_KEY = "local";
  public static final String GLOBAL_RANKING_KEY = "global";
  public static final String POSITION_KEY = "ranking";
  public static final List<String> excluded = Arrays.asList("R", "C");
  private static final String URL = "https://www.meetup.com/pl-PL/topics/";
  private final String name = "Meetup";

  @Override
  public String getName() {
    return name;
  }

  @Override
  public JSONObject getData() {
    StatusLogger.logCollecting("meetup data");

    Map<String, JSONObject> meetupData = new HashMap<>();
    Map<Integer, String> rankingData = new TreeMap<>();
    Map<Integer, String> globalRankingData = new TreeMap<>();

    for (String language : languages) {

      if (excluded.contains(language)) {
        StatusLogger.logSkipped(language, "excluded");
        continue;
      }

      JSONObject languageData = new JSONObject();
      String url = URL + language;
      String urlGlobal = URL + language + "/global";

      try {
        Document doc = Jsoup.connect(url).get();

        int[] numbers = getNumbers(doc);

        if (numbers.length != 2) {
          StatusLogger.logErrorFor("Collected invalid data. Should be 2 numbers (memebers, meetups).");
        } else {
          JSONObject localMeetupData = new JSONObject();
          localMeetupData.put(MEETUPS_KEY, String.format("%,d", numbers[0]));
          localMeetupData.put(MEMBERS_KEY, String.format("%,d", numbers[1]));
          languageData.put(LOCAL_RANKING_KEY, localMeetupData);
          rankingData.put(numbers[1], language);

          Document docGlobal = Jsoup.connect(urlGlobal).get();

          int[] numbersGlobal = getNumbers(docGlobal);

          JSONObject globalMeetupData = new JSONObject();
          globalMeetupData.put(MEETUPS_KEY, String.format("%,d", numbersGlobal[0]));
          globalMeetupData.put(MEMBERS_KEY, String.format("%,d", numbersGlobal[1]));
          languageData.put(GLOBAL_RANKING_KEY, globalMeetupData);
          globalRankingData.put(numbers[1], language);
        }

      } catch (Exception e) {
        StatusLogger.logException(language, e);
      }
      MeetupDataValidator.validate(language, languageData);
      meetupData.put(language, languageData);
    }

    Integer ranking = rankingData.size();
    for (String language : rankingData.values()) {
      ((JSONObject) meetupData.get(language).get(LOCAL_RANKING_KEY)).put(POSITION_KEY, ranking--);
    }

    ranking = globalRankingData.size();
    for (String language : globalRankingData.values()) {
      ((JSONObject) meetupData.get(language).get(GLOBAL_RANKING_KEY)).put(POSITION_KEY, ranking--);
    }

    if (rankingData.equals(globalRankingData)) {
      StatusLogger.logInfo("Local and global ranking is equal.");
    }

    meetupData.put("C", meetupData.get("C++"));//C and C++ are the same
    meetupData.put("R", new JSONObject());//No data for R

    return new JSONObject(meetupData);
  }

  private int[] getNumbers(Document doc) {
    List<String> possibleData = doc.select("#meta p").eachText();

    return possibleData
        .stream()
        .filter(data -> data.matches("^[\\d\\s]*$"))
        .distinct()
        .mapToInt(number -> Integer.parseInt(number.replaceAll("\\s", "")))
        .sorted()
        .toArray();
  }
}
