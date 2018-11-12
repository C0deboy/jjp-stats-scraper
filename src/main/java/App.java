import languageStatistics.FilePersister;
import languageStatistics.Statistics;
import languageStatistics.StatisticsBuilder;
import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.fusesource.jansi.AnsiConsole;
import scrapers.DataScraper;
import scrapers.GithubDataScraper;
import scrapers.LanguageVersionDataScraper;
import scrapers.MeetupDataScraper;
import scrapers.SpectrumDataScraper;
import scrapers.StackOverflowDataScraper;
import scrapers.TiobeIndexDataScraper;
import validators.CompleteStatisticsValidator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class App {

    private static String[] languages = {"C", "C++", "Java", "JavaScript", "Python", "Swift", "R", "Csharp", "Ruby", "PHP"};
    private Set<DataScraper> scrapers = new HashSet<>();

    public static void main(String[] args) {
        enableAnsiColors(args);
        App app = new App();
        app.addScraper(new TiobeIndexDataScraper(languages));
        app.addScraper(new MeetupDataScraper(languages));
        app.addScraper(new StackOverflowDataScraper(languages));
        app.addScraper(new SpectrumDataScraper(languages));
        app.addScraper(new GithubDataScraper(languages));
        app.run();
    }

    public void run() {
        long startTime = System.nanoTime();

        StatisticsBuilder statisticsBuilder = new StatisticsBuilder(languages, scrapers);

        JSONObject completeStatistics = statisticsBuilder.buildStatisticsForEachLanguage();
        CompleteStatisticsValidator.validate(completeStatistics, languages, scrapers);

        FilePersister.saveStatisticsAndKeepOld(completeStatistics, "statistics.json");

        JSONObject languagesVersions = Statistics.build(new LanguageVersionDataScraper(languages));
        FilePersister.saveToFile((JSONObject) languagesVersions.get("data"), "languagesVersions.json");

        long elapsedTime = TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        StatusLogger.logInfo("Done in " + elapsedTime + " seconds.");
    }

    public void addScraper(DataScraper scraper) {
        scrapers.add(scraper);
    }

    private static void enableAnsiColors(String[] args) {
        if (!ArrayUtils.contains(args, "-no-jansi")) {
            AnsiConsole.systemInstall();
        }
    }
}