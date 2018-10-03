package scrapers;

import net.minidev.json.JSONObject;

public interface DataScraper {

    void scrapData();

    String getName();

    JSONObject getData();

}
