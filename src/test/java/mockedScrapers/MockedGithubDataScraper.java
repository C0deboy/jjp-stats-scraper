package mockedScrapers;

import net.minidev.json.JSONObject;
import org.jsoup.Jsoup;
import scrapers.GithubDataScraper;

import java.io.File;
import java.io.IOException;

public class MockedGithubDataScraper extends GithubDataScraper {

    public MockedGithubDataScraper(String[] languages) {
        super(languages);
    }

    @Override
    protected void getProjectsData(String language, JSONObject languageData) throws IOException {
        File file = new File("src/test/resources/github/" + language + "_data.json");
        String doc = Jsoup.parse(file, "UTF-8").text();
        this.extractProjectData(language, languageData, doc);
    }

    @Override
    protected void getMoreThan1000StarsData(String language, JSONObject languageData) throws IOException {
        File file = new File("src/test/resources/github/" + language + "_starsData.json");
        String doc = Jsoup.parse(file, "UTF-8").text();
        this.extractMoreThan1000StarsData(doc, languageData);
    }
}
