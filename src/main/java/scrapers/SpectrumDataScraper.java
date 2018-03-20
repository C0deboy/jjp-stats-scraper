package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import validators.SpectrumDataValidator;

import java.io.InputStream;
import java.util.List;

public class SpectrumDataScraper implements DataScraper {
  public static final String CURRENT_POSITION_KEY = "currentPosition";
  public static final String LAST_YEAR_POSITION_KEY = "lastYearPosition";
  private static final String NAME = "SpectrumRanking";
  private static final String CURRENT_RANKING_FILE = "spectrumRanking2017.html";
  private static final String LAST_YEAR_RANKING_FILE = "spectrumRanking2016.html";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public JSONObject getData() {
    StatusLogger.logCollecting("Spectrum ranking data");

    JSONObject spectrumRankingData = new JSONObject();
    String language = "NONE";
    try {
      InputStream lastYearSpectrumRankingFile = GithubDataScraper.class.getClassLoader().getResourceAsStream(LAST_YEAR_RANKING_FILE);
      InputStream currentSpectrumRankingFile = GithubDataScraper.class.getClassLoader().getResourceAsStream(CURRENT_RANKING_FILE);

      Document doc = Jsoup.parse(currentSpectrumRankingFile, "UTF-8", "");
      Document doc2 = Jsoup.parse(lastYearSpectrumRankingFile, "UTF-8", "");

      List<String> currentSpectrumRanking = doc.select(".language").eachText();
      List<String> lastYearSpectrumRanking = doc2.select(".language").eachText();

      for (int i = 0; i < 20; i++) {

        JSONObject languageData = new JSONObject();

        languageData.put(CURRENT_POSITION_KEY, i + 1);

        language = currentSpectrumRanking.get(i);

        languageData.put(LAST_YEAR_POSITION_KEY, lastYearSpectrumRanking.indexOf(language) + 1);

        SpectrumDataValidator.validate(language, languageData);
        spectrumRankingData.put(language.replace("#", "sharp"), languageData);
      }

      return spectrumRankingData;

    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }

    return spectrumRankingData;
  }
}
