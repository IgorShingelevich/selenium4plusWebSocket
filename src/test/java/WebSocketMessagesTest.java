
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v120.network.Network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class WebSocketMessagesTest implements WebDriverProvider {
    private static DevTools devTools; // Change to static
    private static List<String> websocketMessagesList;
    private static final String TEST_URL = "https://socketsbay.com/test-websockets";

    @BeforeAll
    public static void setup() {
        Configuration.browser = WebSocketMessagesTest.class.getName();
        websocketMessagesList = new ArrayList<>();
    }

    @Test
    public void getWebSocketMessagesTest() {
        // Enable network tracking
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // Capture WebSocket messages
        devTools.addListener(Network.webSocketCreated(), webSocketCreated ->
                System.out.println("WebSocket Created: " + webSocketCreated.getUrl())
        );

        devTools.addListener(Network.webSocketFrameReceived(), webSocketMessage -> {
            String payload = webSocketMessage.getResponse().getPayloadData();
            System.out.println("WebSocket Message Received: " + payload);
            websocketMessagesList.add(payload);
        });

        // Store initial message count
        int initialMessageCount = websocketMessagesList.size();

        // Open the test page
        open(TEST_URL);

        // Press the connect button
        $(By.id("btnConnect")).click();

        // Wait a moment for WebSocket messages
        Selenide.sleep(2000);

        // Assert that new WebSocket messages were received
        assertThat(websocketMessagesList)
                .as("WebSocket messages should increase after button click")
                .hasSizeGreaterThan(initialMessageCount);
    }

    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");

        ChromeDriver driver = new ChromeDriver(options);

        // Initialize DevTools
        devTools = driver.getDevTools();
        devTools.createSession();

        return driver;
    }
}
