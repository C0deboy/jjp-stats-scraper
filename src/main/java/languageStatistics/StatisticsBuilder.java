package languageStatistics;

import net.minidev.json.JSONObject;
import scrapers.DataScraper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class StatisticsBuilder {

    private String[] languages;
    private Set<DataScraper> scrapers;
    private JSONObject statsForEachLanguage = new JSONObject();

    public StatisticsBuilder(String[] languages, Set<DataScraper> scrapers) {
        this.languages = languages;
        this.scrapers = scrapers;
    }

    public JSONObject buildStatisticsForEachLanguage() {
        appendDateToStatistics();

        Set<JSONObject> statisticsSet = new HashSet<>();

        for (DataScraper scraper : scrapers) {
            statisticsSet.add(Statistics.build(scraper));
        }

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


