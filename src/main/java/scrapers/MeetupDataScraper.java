package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import validators.MeetupDataValidator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

public class MeetupDataScraper implements DataScraper {
    public static final String MEETUPS_KEY = "meetups";
    public static final String MEMBERS_KEY = "members";
    public static final String LOCAL_RANKING_KEY = "local";
    public static final String GLOBAL_RANKING_KEY = "global";
    public static final String POSITION_KEY = "ranking";
    public static final List<String> excluded = Arrays.asList("C");
    private static final String URL = "https://www.meetup.com/pl-PL/topics/";
    public static final String NAME = "Meetup";

    private String[] languages;

    private Map<String, JSONObject> meetupData = new ConcurrentHashMap<>();
    private Map<Integer, String> rankingData = new ConcurrentSkipListMap<>();
    private Map<Integer, String> globalRankingData = new ConcurrentSkipListMap<>();

    public MeetupDataScraper(String[] languages) {
        this.languages = languages;
    }

    @Override
    public void scrapData() {
        StatusLogger.logCollecting("meetup data");


        Stream.of(languages).parallel().forEach(this::scrap);


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
    }

    private void scrap(String language) {
        if (excluded.contains(language)) {
            StatusLogger.logSkipped(language, "excluded");
            return;
        }

        String topic = language;
        if (language.equals("R")) {
            topic = "programming-in-r";
        }

        JSONObject languageData = new JSONObject();
        String url = URL + topic;
        String urlGlobal = URL + topic + "/global";

        try {
            Document doc = Jsoup.connect(url).get();

            int[] numbers = getNumbers(doc);

            if (numbers.length != 2) {
                StatusLogger.logError("Collected invalid data. Should be 2 numbers (memebers, meetups).");
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

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public JSONObject getData() {
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
