package de.fayedev.tplinkdevicescraper.service;

import de.fayedev.tplinkdevicescraper.model.ConnectionSubType;
import de.fayedev.tplinkdevicescraper.model.ConnectionType;
import de.fayedev.tplinkdevicescraper.model.TplinkDevice;
import de.fayedev.tplinkdevicescraper.model.TplinkDeviceData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Getter
public class TplinkDeviceScraperService {

    private TplinkDeviceData deviceData;

    private WebDriver getWebDriver() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("fission.webContentIsolationStrategy", 0);
        profile.setPreference("fission.bfcacheInParent", false);
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");
        options.setProfile(profile);
        WebDriver driver = new FirefoxDriver(options);
        driver.manage().window().setSize(new Dimension(3000, 3000));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        return driver;
    }

    private void login(WebDriver driver) throws TimeoutException {
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("local-login-pwd")));

        Actions actions = new Actions(driver);
        actions
                .sendKeys(Keys.TAB)
                .sendKeys(Keys.TAB)
                .sendKeys("YOURPASSWORD")
                .pause(1000)
                .sendKeys(Keys.ENTER)
                .perform();

        boolean clients = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.and(ExpectedConditions.elementToBeClickable(By.id("map-clients")),
                        ExpectedConditions.visibilityOfElementLocated(By.className("map-clients-icon-num"))));

        if (clients) {
            driver.findElement(By.id("map-clients")).click();
        } else {
            throw new ElementNotInteractableException("Login technically not succeeded!");
        }
    }

    private List<TplinkDevice> getTplinkDevices(List<WebElement> tableEntries) {
        List<TplinkDevice> tempDevices = new ArrayList<>();

        for (WebElement row : tableEntries) {
            List<WebElement> info = row
                    .findElement(By.cssSelector(".grid-content-td-deviceName.s-hide"))
                    .findElement(By.className("td-content"))
                    .findElement(By.className("device-info-container"))
                    .findElements(By.tagName("div"));
            WebElement base = getDeviceBase(row);

            if (base != null) {
                ConnectionType type = getConnectionType(base);
                ConnectionSubType subType;
                Integer signalStrength;

                if (info.size() > 3 && type != null) {
                    if (type == ConnectionType.WIFI) {
                        subType = getConnectionSubType(base);
                        signalStrength = getSignalStrength(base);

                        tempDevices.add(new TplinkDevice(info.get(0).getText(), info.get(1).getText(), info.get(2).getText(), type, subType, signalStrength));
                    } else {
                        tempDevices.add(new TplinkDevice(info.get(0).getText(), info.get(1).getText(), info.get(2).getText(), type, null, 5));
                    }
                }
            }
        }
        return tempDevices;
    }

    private WebElement getDeviceBase(WebElement row) {
        WebElement base = null;

        // For Ethernet and 5 Ghz
        try {
            base = row
                    .findElement(By.className("grid-content-td-deviceTag"))
                    .findElement(By.className("td-content"))
                    .findElement(By.className("interface-container"));
        } catch (NoSuchElementException e) {
            // To be expected
        }

        // For 2,4 Ghz
        if (base == null) {
            try {
                base = row
                        .findElement(By.className("grid-content-td-deviceTag"))
                        .findElement(By.className("td-content"))
                        .findElement(By.className("content"))
                        .findElement(By.className("interface-container"));
            } catch (NoSuchElementException e) {
                // To be expected
            }
        }
        return base;
    }

    private ConnectionType getConnectionType(WebElement base) {
        try {
            WebElement type = base
                    .findElement(By.className("icon"));

            if (type.getAttribute("class").contains("icon-interface")) {
                return ConnectionType.ETHERNET;
            } else if (type.getAttribute("class").contains("icon-wireless")) {
                return ConnectionType.WIFI;
            }
        } catch (NoSuchElementException e) {
            // To be expected
        }

        return null;
    }

    private ConnectionSubType getConnectionSubType(WebElement base) {
        try {
            WebElement subType = base
                    .findElement(By.className("text"));

            if (subType.getAttribute("class").contains("text-2g")) {
                return ConnectionSubType.WIFI_2_4;
            } else if (subType.getAttribute("class").contains("text-5g")) {
                return ConnectionSubType.WIFI_5;
            }
        } catch (NoSuchElementException e) {
            // To be expected
        }

        return null;
    }

    private Integer getSignalStrength(WebElement base) {
        try {
            WebElement type = base
                    .findElement(By.className("icon"));

            // We could also write a regex here
            if (type.getAttribute("class").contains("signal-5")) {
                return 5;
            } else if (type.getAttribute("class").contains("signal-4")) {
                return 4;
            } else if (type.getAttribute("class").contains("signal-3")) {
                return 3;
            } else if (type.getAttribute("class").contains("signal-2")) {
                return 2;
            } else if (type.getAttribute("class").contains("signal-1")) {
                return 1;
            }
        } catch (NoSuchElementException e) {
            // To be expected
        }

        return null;
    }

    @Scheduled(fixedDelay = 120000)
    private void scrape() {
        WebDriver driver = getWebDriver();

        try {
            driver.get("http://192.168.0.1/webpages/index.html");

            login(driver);

            List<WebElement> tableEntries = new WebDriverWait(driver, Duration.ofSeconds(10))
                    .until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(
                            By.xpath("//*[@id=\"connected-clients-grid-panel\"]/div/div/div/div[4]/div/div/div[3]/div/table/tbody"),
                            By.className("grid-content-tr")));

            List<TplinkDevice> tempDevices = getTplinkDevices(tableEntries);

            if (!tempDevices.isEmpty()) {
                this.deviceData = new TplinkDeviceData(LocalDateTime.now(), tempDevices);
                log.info("Successfully updated data.");
            } else {
                log.warn("Could not retrieve data. No entries found.");
            }
        } catch (Exception exception) {
            // Happens if someone else is logged in. To be expected. (TimeoutException)
            log.warn("Could not retrieve data. Probably another user is logged in. Current URL: "
                    + driver.getCurrentUrl() + ", Source: ", exception);
        } finally {
            driver.quit();
        }
    }
}
