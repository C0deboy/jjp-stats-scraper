package mockedScrapers;

import org.jsoup.Jsoup;
import scrapers.StackOverflowDataScraper;

import java.io.File;
import java.io.IOException;

public class MockedStackOverflowDataScraper extends StackOverflowDataScraper {

    public MockedStackOverflowDataScraper(String[] languages) {
        super(languages);
    }

    @Override
    protected String fetchData(String language) throws IOException {
        File file = new File("src/test/resources/stackOverflow/" + language + "_data.json");
        return Jsoup.parse(file, "UTF-8").text();
    }
}
