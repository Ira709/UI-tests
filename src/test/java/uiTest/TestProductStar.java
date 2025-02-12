package uiTest;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.codeborne.selenide.Selenide.open;

public class TestProductStar {
    private Locators locators = new Locators();
    private static WebDriver driver;

    private static void configurationRemote() throws URISyntaxException, MalformedURLException {
        Configuration.baseUrl = "https://productstar.ru";
        if(System.getenv("StartRemote").equals("yes")) {
            Configuration.remote = System.getenv("SELENOID_URI");
            Configuration.browser = "chrome";
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--window-size=1920,1080");

        options.setCapability("browserName", "chrome");
        options.setCapability("acceptInsecureCerts", "true");
        options.setCapability("selenoid:options", Map.of(
                "enableVNC", true
        ));
        URI selenoidURI = new URI(Configuration.remote);
        driver = new RemoteWebDriver(selenoidURI.toURL(),options);
    }

    @BeforeAll
    public static void setUp() throws MalformedURLException, URISyntaxException {
        configurationRemote();
    }

    @BeforeEach
    public void prepareForTest(TestInfo testInfo) {
        open("/");
    }

    @AfterAll
    public static void tearDown() {
        Selenide.closeWebDriver();
    }


    @Test
    @DisplayName("Переход в раздел Все программы")
    public void productStarTest() {
        locators.checkTextInTitles("обучение востребованным");
        locators.clickButtonLink("Выбрать направление");
        locators.clickButtonLink("Перейти в каталог");
        locators.checkTextInTitles("Каталог курсов ProductStar");
    }
}

