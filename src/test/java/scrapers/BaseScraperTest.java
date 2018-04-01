package scrapers;

import languageStatistics.FilePersister;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseScraperTest {
  private String[] languages = {"C", "C++", "Java", "JavaScript", "Python", "Swift", "R", "Csharp", "Ruby", "PHP"};
  private DataScraper dataScraper;

  JSONObject scraperData;
  String groupingSeparator;

  BaseScraperTest(DataScraper dataScraper) {
    this.dataScraper = dataScraper;
  }

  @BeforeAll
  void init() {
    dataScraper.scrapDataFor(languages);

    scraperData = dataScraper.getData();
    FilePersister.saveToFile(scraperData, "src/test/statistics/" + dataScraper.getName() + ".json");

    DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
    groupingSeparator = String.valueOf(symbols.getGroupingSeparator());
  }

  @Test
  void scrapedDataSizeIsEqualToNumberOfLanguages() {
    assertThat(scraperData).hasSize(languages.length);
  }

  @Test
  void scrapedDataHasAllLanguages() {
    assertThat(scraperData.keySet()).allSatisfy(langauge -> ArrayUtils.contains(languages, langauge));
  }

  void shouldBeNumeric(String actual, String description) {
    assertThat(actual).as(description).satisfies(StringUtils::isNumeric);
  }
}
