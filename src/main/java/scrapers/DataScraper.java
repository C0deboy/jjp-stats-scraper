package scrapers;

import net.minidev.json.JSONObject;

public interface DataScraper {

  void scrapDataFor(String[] languages);

  String getName();

  JSONObject getData();

}
