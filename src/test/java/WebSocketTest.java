//import com.codeborne.selenide.Selenide;
//import com.codeborne.selenide.WebDriverRunner;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.devtools.DevTools;
//import org.openqa.selenium.devtools.v117.network.Network;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.codeborne.selenide.Selenide.$;
//import static com.codeborne.selenide.Selenide.open;
//
//public class WebSocketTest {
//    private static final String TEST_URL = "https://socketsbay.com/test-websockets";
//
//    @Test
//    @DisplayName("capture ws messages and assert  messages list")
//    public void wsTest() {
//
//        open(TEST_URL);
//
//        // Set up the Chrome DevTools Protocol client
//        WebDriver driver = WebDriverRunner.getWebDriver();
//        DevTools devTools = ((ChromeDriver) driver).getDevTools();
//        devTools.createSession();
//        devTools.send(Network.enable(java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty()));
//
//        // Collect WebSocket messages
//        List<String> webSocketMessages = new ArrayList<>();
//        devTools.addListener(Network.webSocketFrameSent(), frame -> webSocketMessages.add(frame.getResponse().getPayloadData()));
//        devTools.addListener(Network.webSocketFrameReceived(), frame -> webSocketMessages.add(frame.getResponse().getPayloadData()));
//
//        // Perform the click actions and assert the WebSocket messages
//        int initialMessageCount = webSocketMessages.size();
//        clickConnectButton();
//        waitForWebSocketMessages(webSocketMessages, initialMessageCount + 1);
//        Assertions.assertThat(webSocketMessages).hasSize(initialMessageCount + 1);
//
//        clickConnectButton();
//        waitForWebSocketMessages(webSocketMessages, initialMessageCount + 2);
//        Assertions.assertThat(webSocketMessages).hasSize(initialMessageCount + 2);
//
//        clickConnectButton();
//        waitForWebSocketMessages(webSocketMessages, initialMessageCount + 4); // This assumes each click adds 2 messages.
//        Assertions.assertThat(webSocketMessages).hasSize(initialMessageCount + 4);
//
//        // Verify the message content (example of parsing GET/POST methods)
//        int getEventCount = 0;
//        int postEventCount = 0;
//        for (String message : webSocketMessages) {
//            JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
//            if (jsonMessage.has("Method")) {
//                String method = jsonMessage.get("Method").getAsString();
//                if ("GET".equalsIgnoreCase(method)) {
//                    getEventCount++;
//                } else if ("POST".equalsIgnoreCase(method)) {
//                    postEventCount++;
//                }
//            }
//        }
//
//        Assertions.assertThat(getEventCount).isEqualTo(3);
//        Assertions.assertThat(postEventCount).isEqualTo(1);
//
//        // Close the WebDriver after assertions
//        Selenide.closeWebDriver();
//    }
//
//    private static void clickConnectButton() {
//        // Ensure the button is clicked
//        $(By.id("btnConnect")).click();
//    }
//
//    private static void waitForWebSocketMessages(List<String> webSocketMessages, int expectedSize) {
//        // Wait for the expected number of WebSocket messages to be collected
//        long startTime = System.currentTimeMillis();
//        long timeout = 5000; // Timeout in milliseconds
//        while (webSocketMessages.size() < expectedSize && System.currentTimeMillis() - startTime < timeout) {
//            try {
//                Thread.sleep(100); // Sleep for a short time before checking again
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
//        }
//    }
//}
