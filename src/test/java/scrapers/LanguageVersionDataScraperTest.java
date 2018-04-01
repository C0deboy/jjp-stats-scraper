package scrapers;

class LanguageVersionDataScraperTest extends BaseScraperTest {
  String[] languages = {"C", "C++", "Java", "JavaScript", "Python", "Swift", "R", "Csharp", "Ruby", "PHP"};

  LanguageVersionDataScraperTest() {
    super(new LanguageVersionDataScraper());
  }
}