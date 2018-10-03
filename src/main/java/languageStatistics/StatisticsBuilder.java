package languageStatistics;

import net.minidev.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class StatisticsBuilder {

    private Set<JSONObject> statisticsSet = new HashSet<>();
    private String[] languages;
    private JSONObject statsForEachLanguage = new JSONObject();

    public StatisticsBuilder(String[] languages) {
        this.languages = languages;
    }


    public void add(JSONObject statistics) {
        this.statisticsSet.add(statistics);
    }

    public JSONObject buildStatisticsForEachLanguage() {
        appendDateToStatistics();

        for (String language : languages) {

            JSONObject languageData = new JSONObject();

            for (JSONObject statistics : statisticsSet) {

                String name = statistics.getAsString("name");
                JSONObject data = (JSONObject) statistics.get("data");
                JSONObject scraperData = (JSONObject) data.get(language);

                languageData.put(name, scraperData);
                statsForEachLanguage.put(language.replace("++", "pp"), languageData);
            }
        }

        return statsForEachLanguage;
    }

    private void appendDateToStatistics() {
        LocalDate localDate = LocalDate.now();
        statsForEachLanguage.put("date", localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }
}


