import languageStatistics.StatisticsBuilder;
import languageStatistics.StatusLogger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.fusesource.jansi.AnsiConsole;
import scrapers.GithubDataScraper;
import scrapers.LanguagesVersionDataScraper;
import scrapers.MeetupDataScraper;
import scrapers.SpectrumDataScraper;
import scrapers.StackOverFlowDataScraper;
import scrapers.TiobeIndexDataScraper;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;


public class App {
  public static void main(String[] args) {
    long startTime = System.nanoTime();
    enableAnsiColors();

    StatisticsBuilder statisticsBuilder = new StatisticsBuilder();

    String[] languages = {"C", "C++", "Java", "JavaScript", "Python", "Swift", "R", "Csharp", "Ruby", "PHP"};
    statisticsBuilder.collectFor(languages);

    statisticsBuilder.addScraper(new TiobeIndexDataScraper());
    statisticsBuilder.addScraper(new MeetupDataScraper());
    statisticsBuilder.addScraper(new StackOverFlowDataScraper());
    statisticsBuilder.addScraper(new SpectrumDataScraper());
    if(!ArrayUtils.contains(args, "-nogit"))
      statisticsBuilder.addScraper(new GithubDataScraper());

    JSONObject completeStatistics = statisticsBuilder.buildCompleteStatistics();

    StatisticsBuilder.saveStatisticsAndKeepOld(completeStatistics, "statistics.json");

    StatisticsBuilder.saveToFile(new LanguagesVersionDataScraper().getData(), "languagesVersions.json");

    long elapsedTime = TimeUnit.SECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
    StatusLogger.logInfo("Done in " + elapsedTime + " seconds.");
  }

  private static void enableAnsiColors() {
    String classPath = System.getProperty("java.class.path");
    if (!classPath.contains("idea_rt.jar")) {
      AnsiConsole.systemInstall();
    }
  }
}