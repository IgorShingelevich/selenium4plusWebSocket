//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import org.assertj.core.api.Assertions;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import org.openqa.selenium.devtools.DevTools;
//import org.openqa.selenium.devtools.v109.network.Network;
//import io.github.bonigarcia.wdm.WebDriverManager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class WebSocketTestV1 {
//    private static final String TEST_URL = "https://socketsbay.com/test-websockets";
//
//    public static void main(String[] args) {
//        // Automatically download and set up the ChromeDriver
//        WebDriverManager.chromedriver().setup();
//
//        // Set up the WebDriver
//        ChromeOptions options = new ChromeOptions();
//        WebDriver driver = new ChromeDriver(options);
//        driver.get(TEST_URL);
//
//        // Set up the Chrome DevTools Protocol client
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
//        clickConnectButton(driver);
//        Assertions.assertThat(webSocketMessages).hasSize(initialMessageCount + 1);
//
//        clickConnectButton(driver);
//        Assertions.assertThat(webSocketMessages).hasSize(initialMessageCount + 2);
//
//        clickConnectButton(driver);
//        Assertions.assertThat(webSocketMessages).hasSize(initialMessageCount + 4);
//
//        // Verify the message content
//        int getEventCount = 0;
//        int postEventCount = 0;
//        for (String message : webSocketMessages) {
//            JsonObject jsonMessage = JsonParser.parseString(message).getAsJsonObject();
//            if (jsonMessage.get("Method").getAsString().equals("GET")) {
//                getEventCount++;
//            } else if (jsonMessage.get("Method").getAsString().equals("POST")) {
//                postEventCount++;
//            }
//        }
//        Assertions.assertThat(getEventCount).isEqualTo(3);
//        Assertions.assertThat(postEventCount).isEqualTo(1);
//
//        driver.quit();
//    }
//
//    private static void clickConnectButton(WebDriver driver) {
//        driver.findElement(By.id("btnConnect")).click();
//    }
//}