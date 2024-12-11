import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebSocketMessagesTest {
    private WebDriver driver;
    private static final String TEST_URL = "https://socketsbay.com/test-websockets";

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    @Test
    public void getWebSocketMessagesTest() {
        try {
            // Navigate to the test page
            driver.get(TEST_URL);
            System.out.println("Navigated to test page: " + TEST_URL);
            
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            
            // Wait for the server URL input to be visible
            WebElement serverUrlInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input.form-control[placeholder='Server url']")));
            String websocketUrl = serverUrlInput.getAttribute("value");
            System.out.println("WebSocket URL: " + websocketUrl);
            
            // Wait for the connect button to be clickable
            WebElement connectButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-success")));
            System.out.println("Found connect button");
            
            // Click connect button
            connectButton.click();
            System.out.println("Clicked connect button");
            
            // Wait for connection
            Thread.sleep(2000);
            
            // Verify the WebSocket URL
            assertTrue(websocketUrl.contains("wss://socketsbay.com/wss/v2/1/demo/"),
                      "Expected WebSocket URL to match socketsbay.com demo endpoint");
            
            // Get the disconnect button which appears after successful connection
            WebElement disconnectButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".btn-danger")));
            assertTrue(disconnectButton.isDisplayed(), "Expected disconnect button to be visible after connection");
            System.out.println("Connection verified - disconnect button is visible");
            
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
