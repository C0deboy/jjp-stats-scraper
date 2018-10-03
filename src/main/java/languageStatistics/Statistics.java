package languageStatistics;

import net.minidev.json.JSONObject;
import scrapers.DataScraper;

public class Statistics {

    public static JSONObject build(DataScraper dataScraper) {
        dataScraper.scrapData();
        JSONObject statistics = new JSONObject();
        statistics.put("name", dataScraper.getName());
        statistics.put("data", dataScraper.getData());
        return statistics;
    }

}
