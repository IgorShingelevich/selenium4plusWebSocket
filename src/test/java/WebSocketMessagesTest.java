import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v114.network.Network;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketMessagesTest {
    private static final Logger LOGGER = Logger.getLogger(WebSocketMessagesTest.class.getName());
    private WebDriver driver;
    private DevTools devTools;
    private static final String TEST_URL = "https://socketsbay.com/test-websockets";
    private List<String> webSocketMessages;

    @BeforeEach
    public void setup() throws MalformedURLException {
        try {
            LOGGER.info("Setting up ChromeDriver and DevTools...");
            
            // Set ChromeDriver path
            System.setProperty("webdriver.chrome.driver", "P:\\DownloadsHDD\\chromedriver_114.0.5735.90\\chromedriver.exe");
            LOGGER.info("Using ChromeDriver from: P:\\DownloadsHDD\\chromedriver_114.0.5735.90\\chromedriver.exe");
            
            // Set up logging preferences
            LoggingPreferences logPrefs = new LoggingPreferences();
            logPrefs.enable(LogType.BROWSER, Level.ALL);
            logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
            logPrefs.enable(LogType.DRIVER, Level.ALL);
            
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            options.setCapability("goog:loggingPrefs", logPrefs);
            
            // Specify Chrome binary path
            options.setBinary("P:\\DownloadsHDD\\chrome-win32-114\\chrome.exe");
            LOGGER.info("Using Chrome binary from: P:\\DownloadsHDD\\chrome-win32-114\\chrome.exe");
            
            // Create driver directly
            driver = new ChromeDriver(options);
            LOGGER.info("ChromeDriver initialized successfully");
            
            // Initialize DevTools
            devTools = ((ChromeDriver) driver).getDevTools();
            devTools.createSession();
            webSocketMessages = new ArrayList<>();
            
            // Enable network tracking
            LOGGER.info("Enabling network tracking...");
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            LOGGER.info("Network tracking enabled");

            // Listen for WebSocket handshake requests
            devTools.addListener(Network.webSocketCreated(), webSocket -> {
                LOGGER.info("WebSocket Created - URL: " + webSocket.getUrl());
                if (webSocket.getInitiator().isPresent()) {
                    LOGGER.info("WebSocket Details - Initiator: " + webSocket.getInitiator().get());
                }
            });

            // Listen for WebSocket frames being sent
            devTools.addListener(Network.webSocketFrameSent(), frame -> {
                String message = frame.getResponse().getPayloadData();
                LOGGER.info("WebSocket Frame Sent: " + message);
                webSocketMessages.add("SENT: " + message);
            });

            // Listen for WebSocket frames being received
            devTools.addListener(Network.webSocketFrameReceived(), frame -> {
                String message = frame.getResponse().getPayloadData();
                LOGGER.info("WebSocket Frame Received: " + message);
                webSocketMessages.add("RECEIVED: " + message);
            });

            // Listen for WebSocket errors
            devTools.addListener(Network.webSocketFrameError(), error -> {
                LOGGER.severe("WebSocket Error: " + error.getErrorMessage());
                webSocketMessages.add("ERROR: " + error.getErrorMessage());
            });

            driver.manage().window().maximize();
            LOGGER.info("Browser window maximized");
            LOGGER.info("Setup completed successfully");
        } catch (Exception e) {
            LOGGER.severe("Setup failed: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void getWebSocketMessagesTest() {
        try {

            driver.get(TEST_URL);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            WebElement connectButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-success")));
            connectButton.click();

            Thread.sleep(500);

            LOGGER.info("=== Captured WebSocket Messages ===");
            webSocketMessages.forEach(msg -> LOGGER.info(msg));
            LOGGER.info("=== End of WebSocket Messages ===");

            System.out.println("ws messages total: " + webSocketMessages.size());
            System.out.println("ws messages: \r\n" + webSocketMessages);

        } catch (Exception e) {
            LOGGER.severe("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        LOGGER.info("Starting teardown...");
        if (devTools != null) {
            try {
                devTools.close();
                LOGGER.info("DevTools session closed");
            } catch (Exception e) {
                LOGGER.warning("Error closing DevTools: " + e.getMessage());
            }
        }
        if (driver != null) {
            try {
                driver.quit();
                LOGGER.info("WebDriver quit successfully");
            } catch (Exception e) {
                LOGGER.warning("Error quitting WebDriver: " + e.getMessage());
            }
        }
        LOGGER.info("Teardown complete");
    }
}
