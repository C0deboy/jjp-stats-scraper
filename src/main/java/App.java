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

    Statistics statistics = new Statistics();
    statistics.collectFor(languages);

    StatisticsBuilder statisticsBuilder = new StatisticsBuilder();
    statisticsBuilder.assignStatsForEachLanguage(languages);

    Set<DataScraper> scrapers = new HashSet<>();
    scrapers.add(new TiobeIndexDataScraper());
    scrapers.add(new MeetupDataScraper());
    scrapers.add(new StackOverflowDataScraper());
    scrapers.add(new SpectrumDataScraper());

    if (!ArrayUtils.contains(args, "-no-git")) {
      scrapers.add(new GithubDataScraper());
    }

    for (DataScraper scraper : scrapers) {
      statisticsBuilder.add(statistics.build(scraper));
    }

    JSONObject completeStatistics = statisticsBuilder.buildMergedStatistics();
    CompleteStatisticsValidator.validate(completeStatistics, languages, scrapers);
    FilePersister.saveStatisticsAndKeepOld(completeStatistics, "statistics.json");

    JSONObject languagesVersion = statistics.build(new LanguageVersionDataScraper());
    FilePersister.saveToFile((JSONObject) languagesVersion.get("data"), "languagesVersions.json");

    long elapsedTime = TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    StatusLogger.logInfo("Done in " + elapsedTime + " seconds.");
  }

  private static void enableAnsiColors(String[] args) {
    if (!ArrayUtils.contains(args, "-no-jansi")) {
      AnsiConsole.systemInstall();
    }
  }
}