package uiTest;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.exporter.PushGateway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;

@ExtendWith(ExecutionWatcher.class)
public class TestProductStar {
    private static WebDriver driver;

    static CollectorRegistry registry;
    static Counter requests;
    public static Counter failedRequests;
    public static Counter passedRequest;

    private static void configurationRemote() throws URISyntaxException, MalformedURLException {
        Configuration.baseUrl = "https://demoqa.com";

        String startRemote = System.getenv("startRemote");
        String selenoidUri = System.getenv("SELENOID_URI");


        if ("yes".equals(startRemote) && selenoidUri != null) {
            Configuration.remote = System.getenv("SELENOID_URI");
            Configuration.browser = "chrome";


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
            driver = new RemoteWebDriver(selenoidURI.toURL(), options);
            }
        }

    @BeforeAll
    public static void setUp() throws MalformedURLException, URISyntaxException {
        Configuration.browser = "chrome";
        Configuration.timeout = 5000;
        configurationRemote();

        registry = new CollectorRegistry();
        requests = Counter.build()
                .name("total_tests")
                .help("Number of tests run")
                .register(registry);
        failedRequests = Counter.build()
                .name("failed_tests")
                .help("Number of failed tests")
                .register(registry);
        passedRequest = Counter.build()
                .name("passed_tests")
                .help("Number of passed tests")
                .register(registry);
    }

    @BeforeEach
    public void prepareForTest(TestInfo testInfo) {
        open("/");
    }

    @AfterAll
    public static void tearDown() throws IOException {
        Selenide.closeWebDriver();
        PushGateway pg = new PushGateway("127.0.0.1:9091");
        pg.push(registry, "my_batch_job");
    }

    @AfterEach
    public void testTearDown(TestInfo testInfo) {
        requests.inc();
        if(testInfo.getTags().contains("failed")) {
            failedRequests.inc();
        } else {
            passedRequest.inc();
        }
    }


    @Test
    @DisplayName("Поиск Text Box")
    public void productStarTest1() {
        SelenideElement elements = $x("//div[@class='card mt-4 top-card' and .//h5[text()='Elements']]");
        elements.shouldBe(Condition.visible).click();

        SelenideElement findTextBox = $("#item-0");
        findTextBox.shouldBe(Condition.visible);
    }

    @Test
    @DisplayName("Поиск Practice Form")
    public void productStarTest2() {
       SelenideElement cardForms = $x("//div[@class='card mt-4 top-card' and .//h5[text()='Forms']]");
       cardForms.shouldBe(Condition.visible).click();

       SelenideElement practiceForm = $("//span[text()='Practice Form']");
       practiceForm.shouldBe(Condition.visible).click();
    }
}

