package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import validators.TiobeIndexDataValidator;

import java.util.List;

public class TiobeIndexDataScraper implements DataScraper {
  public static final String CURRENT_POSITION_KEY = "currentPosition";
  public static final String LAST_YEAR_POSITION_KEY = "lastYearPosition";
  private static final String NAME = "TiobeIndex";
  private static final String URL = "https://www.tiobe.com/tiobe-index/";
  private static final String TABLE_TOP20_ROWS = ".table-top20 tbody tr";
  private static final String LAST_YEAR_POSITION_TD = "td:nth-child(2)";
  private static final String LANGUAGE_TD = "td:nth-child(4)";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public JSONObject getData() {
    StatusLogger.logCollecting("Tiobe index data");


    JSONObject tiobeIndexData = new JSONObject();

    String language = "NONE";

    try {
      Document doc = Jsoup.connect(URL).get();

      Elements possibleRows = doc.select(TABLE_TOP20_ROWS);
      List<String> lastYearPositions = possibleRows.select(LAST_YEAR_POSITION_TD).eachText();
      List<String> languages = possibleRows.select(LANGUAGE_TD).eachText();

      for (int i = 0; i < languages.size(); i++) {
        language = languages.get(i).replace("#", "sharp");

        try {
          JSONObject languageData = new JSONObject();
          languageData.put(CURRENT_POSITION_KEY, i + 1);
          languageData.put(LAST_YEAR_POSITION_KEY, Integer.parseInt(lastYearPositions.get(i).trim()));

          TiobeIndexDataValidator.validate(language, languageData);
          tiobeIndexData.put(language, languageData);

        } catch (NumberFormatException e) {
          StatusLogger.logSkipped(language, e.getMessage());
        }

      }

      return tiobeIndexData;

    } catch (Exception e) {
      StatusLogger.logException(language, e);
    }

    return tiobeIndexData;
  }
}
