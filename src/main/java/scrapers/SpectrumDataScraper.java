package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import validators.SpectrumDataValidator;

import java.io.InputStream;
import java.util.List;

public class SpectrumDataScraper implements DataScraper {
    public static final String CURRENT_POSITION_KEY = "currentPosition";
    public static final String LAST_YEAR_POSITION_KEY = "lastYearPosition";
    public static final String RANK_DATA = ".rank_column";
    public static final String RANK_DATA_LANGUAGE = ".language";
    public static final String RANK_DATA_RANK = ".rank";
    public static final String NAME = "SpectrumRanking";
    private static final String CURRENT_RANKING_FILE = "spectrumRanking2018.html";
    private static final String LAST_YEAR_RANKING_FILE = "spectrumRanking2017.html";
    private String[] languages;
    private JSONObject spectrumRankingData = new JSONObject();

    public SpectrumDataScraper(String[] languages) {
        this.languages = languages;
    }

    @Override
    public void scrapData() {
        StatusLogger.logCollecting("Spectrum ranking data");

        String language = "NONE";
        try {
            InputStream lastYearSpectrumRankingFile = GithubDataScraper.class.getClassLoader().getResourceAsStream(LAST_YEAR_RANKING_FILE);
            InputStream currentSpectrumRankingFile = GithubDataScraper.class.getClassLoader().getResourceAsStream(CURRENT_RANKING_FILE);

            Document currentYearDoc = Jsoup.parse(currentSpectrumRankingFile, "UTF-8", "");
            Document lastYearDoc = Jsoup.parse(lastYearSpectrumRankingFile, "UTF-8", "");

            Elements currentSpectrumRanking = currentYearDoc.select(RANK_DATA);
            List<String> lastYearSpectrumRanking = lastYearDoc.select(RANK_DATA_LANGUAGE).eachText();
            lastYearSpectrumRanking.replaceAll(language2 -> language2.replace("#", "sharp"));

            for (Element element : currentSpectrumRanking) {
                language = element.select(RANK_DATA_LANGUAGE).text().replace("#", "sharp");

                if (ArrayUtils.contains(languages, language)) {
                    JSONObject languageData = new JSONObject();
                    int currentPosition = Integer.parseInt(element.select(RANK_DATA_RANK).text().replace(".", ""));
                    languageData.put(CURRENT_POSITION_KEY, currentPosition);
                    languageData.put(LAST_YEAR_POSITION_KEY, lastYearSpectrumRanking.indexOf(language) + 1);

                    SpectrumDataValidator.validate(language, languageData);
                    spectrumRankingData.put(language, languageData);
                }
            }

        } catch (Exception e) {
            StatusLogger.logException(language, e);
        }

    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public JSONObject getData() {
        return spectrumRankingData;
    }
}
