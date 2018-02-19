package languageStatistics;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import scrapers.DataScraper;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatisticsBuilder {

  //All by default
  public static String[] languages = {"C", "C++", "Java", "JavaScript", "Python", "Swift", "R", "Csharp", "Ruby", "PHP"};
  private JSONObject completeStatistics = new JSONObject();
  private ArrayList<DataScraper> scrapers = new ArrayList<>();

  static public void saveToFile(JSONObject jsonObject, String filename) {

    try (Writer out = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(filename), "UTF-8"))) {
      out.write(jsonObject.toJSONString(JSONStyle.LT_COMPRESS));
      StatusLogger.gap();
      StatusLogger.logInfo("File " + filename + " saved.");
    } catch (IOException e) {
      StatusLogger.logException("Saving file " + filename, e);
    }
  }

  public static void saveStatisticsAndKeepOld(JSONObject completeStatistics, String filename) {
      Path fileToMovePath = Paths.get(filename);
      if (Files.notExists(fileToMovePath)) {
        saveToFile(completeStatistics, filename);
      }
      else {
        try (Reader reader = Files.newBufferedReader(fileToMovePath)) {
          JSONObject oldStatistics = (JSONObject) JSONValue.parse(reader);
          String oldStatisticDate = oldStatistics.getAsString("date");

          String newFileName = appendDateToFileName(filename, oldStatisticDate);
          Path targetPath = Paths.get(newFileName);
          Files.move(fileToMovePath, targetPath);

          StatusLogger.logInfo("Old statistics file renamed to: " + newFileName);
          saveToFile(completeStatistics, filename);
        } catch (IOException e) {
          StatusLogger.logException("Renaming file " + filename, e);
        }
      }
  }

  private static String appendDateToFileName(String filename, String oldStatisticDate) {
    String[] fileData = filename.split("\\.");
    String name = fileData[0];
    String extension = fileData[1];
    return name + "-" + oldStatisticDate + "." + extension;
  }

  public void addScraper(DataScraper dataScraper) {
    scrapers.add(dataScraper);
  }

  public void collectFor(String[] langs) {
    languages = langs;
  }

  public JSONObject buildCompleteStatistics() {
    appendDateToStatistics();

    Map<String, JSONObject> data = new HashMap<>();

    for (DataScraper scraper : scrapers) {
      data.put(scraper.getName(), scraper.getData());
    }

    for (String language : languages) {

      Map<String, JSONObject> languageData = new HashMap<>();

      for (Map.Entry<String, JSONObject> stats : data.entrySet()) {
        languageData.put(stats.getKey(), (JSONObject) stats.getValue().get(language));
      }

      completeStatistics.put(language.replace("+", "p"), languageData);
    }

    return completeStatistics;
  }

  private void appendDateToStatistics() {
    Date date = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    completeStatistics.put("date", sdf.format(date));
  }
}


