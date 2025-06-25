package meraldaCallsWithCodePush;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestMeraldaCalls {

            private void sendSlackNotification(String message) {
        try {
            String webhookUrl = System.getenv("SLACK_WEBHOOK_URL");
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            String payload = "{\"text\": \"" + message + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.getBytes());
                os.flush();
            }
            conn.getResponseCode();
        } catch (Exception e) {
            System.out.println("Failed to send Slack notification: " + e.getMessage());
        }
    }


    @Test
    public void testVideoCallFlow() throws Exception  {

        System.out.println("Setting Firefox media preferences...");
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("media.navigator.permission.disabled", true);
        options.addPreference("media.navigator.streams.fake", true);
        options.addPreference("media.peerconnection.enabled", true);

        System.out.println("Launching Firefox driver...");
        WebDriver driver = new FirefoxDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        Actions actions = new Actions(driver);

        System.out.println("Maximizing browser window...");
        driver.manage().window().maximize();

        try {
            System.out.println("Navigating to Meralda website...");
            driver.get("https://meralda.scalenext.io/");
            System.out.println("URL opened: " + driver.getCurrentUrl());
            Assert.assertEquals("https://meralda.scalenext.io/", driver.getCurrentUrl());

            System.out.println("Waiting and clicking on 'Jewellery'...");
            WebElement jewellery = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"homeMainnavBarDrop\"]/div/ul/li[2]/a")));
            jewellery.click();
            System.out.println("'Jewellery' clicked");

            System.out.println("Waiting and clicking on 'Necklaces and Pendants'...");
            WebElement necklaces = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"homeMainnavBarDrop\"]/div/ul/li[2]/div/div/div/ul[1]/li[2]/a")));
            necklaces.click();
            System.out.println("'Necklaces and Pendants' clicked");

            System.out.println("Waiting for products to load...");
            Thread.sleep(4000);

            System.out.println("Locating the first product...");
            WebElement firstProduct = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"homeMainContent\"]/section[2]/div/div[2]/div/div/div[1]/div/div/div[1]/div[1]/div/div[3]/a/div/img")));

            System.out.println("Scrolling to the first product...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", firstProduct);
            Thread.sleep(1500);
            System.out.println("Clicking on the first product...");
            actions.moveToElement(firstProduct).click().perform();

            Thread.sleep(4000);

            System.out.println("Locating video call button...");
            WebElement videoCallBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"homeMainContent\"]/section[1]/div/div/div[2]/div/form/div[3]/button[1]")));

            System.out.println("Scrolling to video call button...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", videoCallBtn);
            Thread.sleep(5000);
            System.out.println("Clicking video call button using JavaScript...");
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", videoCallBtn);

            System.out.println("Waiting for iframe to appear...");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
            System.out.println("Iframe appeared");

            System.out.println("Switching to iframe...");
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));

            System.out.println("Waiting for loading overlay to disappear...");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.loading-page")));
            System.out.println("Loading overlay gone");

            System.out.println("Locating Name input field...");
            WebElement nameInput = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Name']")));
            Assert.assertTrue(nameInput.isDisplayed());

            System.out.println("Locating Number input field...");
            WebElement numberInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"inlineFormInputGroup\"]")));
            Assert.assertTrue(numberInput.isDisplayed());
            
            Thread.sleep(4000);

            System.out.println("Entering name, number, and pin code...");
            nameInput.click();
            nameInput.sendKeys("Prachi Test");
            numberInput.click();
            numberInput.sendKeys("8435627503");
          

            System.out.println("Clicking confirm button...");
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"popin-panel\"]/div/div[3]/div[1]/div/div/div/button")));
            confirmBtn.click();

            Thread.sleep(4000);

            System.out.println("Waiting for 'Video Call Now' and 'Schedule Call Later' buttons...");
            WebElement video_call_now = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"popin-panel\"]/div/div[3]/div[1]/div/button[1]")));
            WebElement schedule_call_later = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"popin-panel\"]/div/div[3]/div[1]/div/button[2]")));

            System.out.println("Verifying 'Video Call Now' and 'Schedule Call Later' buttons...");
            Assert.assertTrue(video_call_now.isDisplayed() && video_call_now.isEnabled());
            Assert.assertTrue(schedule_call_later.isDisplayed() && schedule_call_later.isEnabled());

            System.out.println("Clicking on 'Video Call Now' button...");
            video_call_now.click();
            Thread.sleep(3000);

            System.out.println("Switching back to iframe...");
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));
            Thread.sleep(3000);

            System.out.println("Locating 'Allow Access' button...");
            WebElement allowAccessBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"popin-panel\"]/div/div[3]/div[1]/div/div/div/button")));
            Assert.assertTrue(allowAccessBtn.isDisplayed() && allowAccessBtn.isEnabled());

            System.out.println("Clicking 'Allow Access'...");
            allowAccessBtn.click();

            System.out.println("Switching to iframe again after access...");
            driver.switchTo().defaultContent();
            Thread.sleep(3000);
            driver.switchTo().frame(0);

            System.out.println("Waiting for call connected UI...");
            WebElement callConnected = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"popin-panel\"]/div/div/div[1]/div/div/div/div[3]/div[2]/ul/li[4]")));
            Assert.assertTrue(callConnected.isDisplayed());

            System.out.println("Verifying video call UI buttons...");
            Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"popin-panel\"]/div/div/div[1]/div/div/div/div[3]/div[2]/ul/li[1]"))).isDisplayed(), "Camera icon missing");
            Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"popin-panel\"]/div/div/div[1]/div/div/div/div[3]/div[2]/ul/li[2]"))).isDisplayed(), "Mute icon missing");
            Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"popin-panel\"]/div/div/div[1]/div/div/div/div[3]/div[2]/ul/li[3]"))).isDisplayed(), "Chat icon missing");

            System.out.println("Locating and clicking 'Cut Call'...");
            WebElement cutCall = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"popin-panel\"]/div/div/div[1]/div/div/div/div[3]/div[2]/ul/li[4]")));
            Assert.assertTrue(cutCall.isDisplayed());

            System.out.println("Clicking on 'Three Dots' menu...");
            WebElement threeDots = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"popin-panel\"]/div/div/div[1]/div/div/div/div[3]/div[2]/ul/li[5]")));
            threeDots.click();

            System.out.println("Checking screen share and zoom...");
            Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"popin-panel\"]/div/div/div[1]/div/div/div/div[3]/div[2]/ul/li[5]/ul/li[1]"))).isDisplayed());
            Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"popin-panel\"]/div/div/div[1]/div/div/div/div[3]/div[2]/ul/li[5]/ul/li[2]"))).isDisplayed());

            System.out.println("Ending call...");
            cutCall.click();

            System.out.println("Switching to iframe for rating...");
            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.tagName("iframe")));

            System.out.println("Waiting for rating screen...");
            WebElement ratingTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'Please rate your call.')]")));
            Assert.assertTrue(ratingTitle.isDisplayed());

            System.out.println("Clicking 5-star rating...");
            WebElement fiveStar = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//label[@for='rate5']")));
            fiveStar.click();

            System.out.println("Filling comment box...");
            WebElement commentBox = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[@id=\"popin-panel\"]/div/div[3]/div[1]/div/div/textarea")));
            commentBox.sendKeys("Great call on Firefox!");

            System.out.println("Submitting rating...");
            WebElement submitRating = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"popin-panel\"]/div/div[3]/div[1]/div/div/button")));
            submitRating.click();

            System.out.println("Checking for thank you message...");
            WebElement thankYouMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),\"We're thrilled to be part of your pleasure journey.\")]")));
            Assert.assertTrue(thankYouMsg.isDisplayed());

            System.out.println("Clicking 'Continue Browsing'...");
            WebElement continueBrowsing = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//*[@id=\"popin-panel\"]/div/div[3]/div[1]/div/div/div[2]/button")));
            continueBrowsing.click();

            System.out.println("✅ Test completed successfully!");

            sendSlackNotification("✅ Test Passed: Video call flow completed successfully.");

        } catch (Exception e) {
            System.out.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("Closing browser...");
            driver.quit();
        }
    }
}
