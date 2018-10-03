package languageStatistics;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilePersister {


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
        } else {
            try (Reader reader = Files.newBufferedReader(fileToMovePath)) {
                JSONObject oldStatistics = (JSONObject) JSONValue.parse(reader);
                String oldStatisticDate = oldStatistics.getAsString("date");

                String newFileName = appendToFileName(filename, oldStatisticDate);
                Path targetPath = Paths.get(newFileName);
                while(Files.exists(targetPath)) {
                    newFileName = appendToFileName(filename,"OLD");
                    targetPath = Paths.get(newFileName);
                }
                Files.move(fileToMovePath, targetPath);
                StatusLogger.logInfo("Old statistics file renamed to: " + newFileName);
                saveToFile(completeStatistics, filename);
            } catch (IOException e) {
                StatusLogger.logException("Renaming file " + filename, e);
            }
        }
    }

    private static String appendToFileName(String filename, String oldStatisticDate) {
        String[] fileData = filename.split("\\.");
        String name = fileData[0];
        String extension = fileData[1];
        return name + "_" + oldStatisticDate + "." + extension;
    }
}
