package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LanguageVersionDataScraper implements DataScraper {
    public static final String RELEASE_INFO_KEY = "releaseInfo";
    public static final String RELEASE_DATE_KEY = "releaseDate";
    public static final String VERSION_KEY = "version";
    public static final String NAME = "LanguagesVersion";
    public static final String WIKI_INFOBOX = "table.infobox tr";
    public static final String VERSION_HEADER = "Stable release";
    public static final int INDEX_OF_JAVA_TD_AT_PL_WIKI = 7;

    private Map<String, JSONObject> langsVersionData = new ConcurrentHashMap<>();
    private String currentLanguage;
    private String[] languages;

    public LanguageVersionDataScraper(String[] languages) {
        this.languages = languages;
    }

    @Override
    public void scrapData() {
        StatusLogger.logCollecting("Languages version data");
        Stream.of(languages).parallel().forEach(this::scrap);
    }

    private void scrap(String language) {
        this.currentLanguage = language;

        JSONObject languageData = new JSONObject();
        String commonUrl = "https://en.wikipedia.org/wiki/" + language + "_(programming_language)";

        Map<String, String> specificUrls = new HashMap<>();
        specificUrls.put("Java", "https://pl.wikipedia.org/wiki/Java");
        specificUrls.put("JavaScript", "https://en.wikipedia.org/wiki/JavaScript");
        specificUrls.put("Csharp", "https://en.wikipedia.org/wiki/C_Sharp_(programming_language)");
        specificUrls.put("C++p", "https://en.wikipedia.org/wiki/C%2B%2B");

        if (specificUrls.containsKey(language)) {
            commonUrl = specificUrls.get(language);
        }

        try {
            Document doc = Jsoup.connect(commonUrl).get();
            Elements wikiTable = doc.select(WIKI_INFOBOX);
            wikiTable = wikiTable.next();
            List<String> th = wikiTable.select("th").eachText();
            List<String> td = wikiTable.select("td").eachText();

            int index = th.indexOf(VERSION_HEADER);

            if (language.equals("Java")) {
                index = INDEX_OF_JAVA_TD_AT_PL_WIKI;
            }

            String latestReleaseInfo = td.get(index);

            languageData.put(RELEASE_INFO_KEY, latestReleaseInfo);

            String releaseDate = getDateFromReleaseInfo(latestReleaseInfo);
            languageData.put(RELEASE_DATE_KEY, releaseDate);

            String version = getVersionFromReleaseInfo(latestReleaseInfo);
            languageData.put(VERSION_KEY, version);

            langsVersionData.put(language.replace("+", "p"), languageData);

            StatusLogger.logSuccessFor(language);

        } catch (Exception e) {
            StatusLogger.logException(currentLanguage, e);
        }
    }

    private String getVersionFromReleaseInfo(String latestReleaseInfo) {
        Pattern versionPattern = Pattern.compile("\\d+\\.\\d+(\\.\\d+)?");
        Matcher versionMatcher = versionPattern.matcher(latestReleaseInfo);
        String version = "";

        if (versionMatcher.find()) {
            version = versionMatcher.group();
        } else {
            try {
                version = latestReleaseInfo.split(" /")[0].trim().replaceAll("\\[.*]", "");
                StatusLogger.appendWarning("Not plain version, using: " + version);
            } catch (ArrayIndexOutOfBoundsException e) {
                StatusLogger.logError("Cannot retrieve version from release info.");
            }
        }
        return version;
    }

    private String getDateFromReleaseInfo(String latestReleaseInfo) {
        Pattern datePattern = Pattern.compile("\\(\\d+-\\d+-?\\d*\\)");
        Matcher matcher = datePattern.matcher(latestReleaseInfo);
        String releaseDate = "";

        if (matcher.find()) {
            releaseDate = matcher.group().replaceAll("[()]", "");
        } else {
            Pattern fullDatePattern = Pattern.compile("\\d+ \\w+.+\\d{4}");
            Matcher fullDateMatcher = fullDatePattern.matcher(latestReleaseInfo);
            if (fullDateMatcher.find()) {
                releaseDate = fullDateMatcher.group();
            } else {
                StatusLogger.logErrorFor(currentLanguage, "Cannot retrieve date from release info.");
            }
        }

        try {
            DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("pl"));
            try {
                return LocalDate.parse(releaseDate).format(fullDateFormatter);
            } catch (DateTimeParseException e) {
                return LocalDate.parse(releaseDate, fullDateFormatter).format(fullDateFormatter);
            }
        } catch (DateTimeParseException e) {
            YearMonth yearMonth = YearMonth.parse(releaseDate, DateTimeFormatter.ofPattern("yyyy-MM"));
            String month = yearMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pl"));
            switch (month) {
                case "listopada":
                    month = "listopadzie";
                    break;
                case "lutego":
                    month = "lutym";
                    break;
                default:
                    month = month.substring(0, month.length() - 1) + "u ";
            }
            String date = "w " + month + yearMonth.getYear();
            StatusLogger.appendWarning("Not full date, using: " + date);
            return date;
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public JSONObject getData() {
        return new JSONObject(langsVersionData);
    }
}
