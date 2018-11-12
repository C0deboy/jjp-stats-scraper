import mockedScrapers.MockedGithubDataScraper;
import mockedScrapers.MockedStackOverflowDataScraper;
import org.junit.jupiter.api.Test;
import scrapers.DataScraper;
import scrapers.MeetupDataScraper;
import scrapers.SpectrumDataScraper;
import scrapers.TiobeIndexDataScraper;

import java.util.HashSet;
import java.util.Set;

class AppTest {
    private static String[] languages = {"C", "C++", "Java", "JavaScript", "Python", "Swift", "R", "Csharp", "Ruby", "PHP"};

    @Test
    void main() {
        App app = new App();
        app.addScraper(new TiobeIndexDataScraper(languages));
        app.addScraper(new MeetupDataScraper(languages));
        app.addScraper(new MockedStackOverflowDataScraper(languages));
        app.addScraper(new SpectrumDataScraper(languages));
        app.addScraper(new MockedGithubDataScraper(languages));
        app.run();
    }
}