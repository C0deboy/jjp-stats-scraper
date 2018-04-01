package languageStatistics;

import net.minidev.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class StatisticsBuilder {

  private Set<JSONObject> statisticsSet = new HashSet<>();
  private String[] languages;
  private JSONObject mergedStatistics = new JSONObject();

  public void assignStatsForEachLanguage(String[] languages) {
    this.languages = languages;
  }


  public void add(JSONObject statistics) {
    this.statisticsSet.add(statistics);
  }

  public JSONObject buildMergedStatistics() {
    appendDateToStatistics();

    for (String language : languages) {

      JSONObject languageData = new JSONObject();

      for (JSONObject statistics : statisticsSet) {

        String name = statistics.getAsString("name");
        JSONObject data = (JSONObject) statistics.get("data");
        JSONObject scraperData = (JSONObject) data.get(language);

        languageData.put(name, scraperData);
        mergedStatistics.put(language.replace("++", "pp"), languageData);
      }
    }

    return mergedStatistics;
  }

  private void appendDateToStatistics() {
    Date date = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    mergedStatistics.put("date", sdf.format(date));
  }
}


