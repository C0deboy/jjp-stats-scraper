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
    public static void main(String[] args) {
        long startTime = System.nanoTime();
        enableAnsiColors(args);

        String[] languages = {"C", "C++", "Java", "JavaScript", "Python", "Swift", "R", "Csharp", "Ruby", "PHP"};

        StatisticsBuilder statisticsBuilder = new StatisticsBuilder(languages);

        Set<DataScraper> scrapers = new HashSet<>();
        scrapers.add(new TiobeIndexDataScraper(languages));
        scrapers.add(new MeetupDataScraper(languages));
        scrapers.add(new StackOverflowDataScraper(languages));
        scrapers.add(new SpectrumDataScraper(languages));
        scrapers.add(new GithubDataScraper(languages));

        for (DataScraper scraper : scrapers) {
            statisticsBuilder.add(Statistics.build(scraper));
        }

        JSONObject completeStatistics = statisticsBuilder.buildStatisticsForEachLanguage();
        CompleteStatisticsValidator.validate(completeStatistics, languages, scrapers);

        FilePersister.saveStatisticsAndKeepOld(completeStatistics, "statistics.json");

        JSONObject languagesVersions = Statistics.build(new LanguageVersionDataScraper(languages));
        FilePersister.saveToFile((JSONObject) languagesVersions.get("data"), "languagesVersions.json");

        long elapsedTime = TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        StatusLogger.logInfo("Done in " + elapsedTime + " seconds.");
    }

    private static void enableAnsiColors(String[] args) {
        if (!ArrayUtils.contains(args, "-no-jansi")) {
            AnsiConsole.systemInstall();
        }
    }
}