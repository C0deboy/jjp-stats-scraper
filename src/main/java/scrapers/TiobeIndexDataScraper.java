package scrapers;

import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import validators.TiobeIndexDataValidator;

public class TiobeIndexDataScraper implements DataScraper {
  public static final String CURRENT_POSITION_KEY = "currentPosition";
  public static final String LAST_YEAR_POSITION_KEY = "lastYearPosition";
  public static final String NAME = "TiobeIndex";
  private static final String URL = "https://www.tiobe.com/tiobe-index/";
  private static final String TABLE_TOP20_ROWS = ".table-top20 tbody tr";
  private static final String LAST_YEAR_POSITION_TD = "td:nth-child(2)";
  private static final String CURRENT_YEAR_POSITION_TD = "td:nth-child(1)";
  private static final String LANGUAGE_TD = "td:nth-child(4)";

  private JSONObject tiobeIndexData = new JSONObject();

  @Override
  public void scrapDataFor(String[] languages) {
    StatusLogger.logCollecting("Tiobe index data");

    String language = "NONE";

    try {
      Document doc = Jsoup.connect(URL).get();

      Elements possibleRows = doc.select(TABLE_TOP20_ROWS);

      for (Element row : possibleRows) {
        language = row.select(LANGUAGE_TD).text().replace("#", "sharp");

        if (ArrayUtils.contains(languages, language)) {

          int currentYearPosition = Integer.parseInt(row.select(CURRENT_YEAR_POSITION_TD).text());
          int lastYearPosition = Integer.parseInt(row.select(LAST_YEAR_POSITION_TD).text());
          JSONObject languageData = new JSONObject();
          languageData.put(CURRENT_POSITION_KEY, currentYearPosition);
          languageData.put(LAST_YEAR_POSITION_KEY, lastYearPosition);

          TiobeIndexDataValidator.validate(language, languageData);
          tiobeIndexData.put(language, languageData);
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
    return tiobeIndexData;
  }
}
