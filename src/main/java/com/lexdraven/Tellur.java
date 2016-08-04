package com.lexdraven;

import com.lexdraven.services.BrowserDriverKeeper;
import com.lexdraven.services.OwnExceptionHandler;
import com.lexdraven.services.ScreenShooter;
import com.lexdraven.services.WebEventListener;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class Tellur {
    private EventFiringWebDriver webDriver;
    private int timeToWait = 3;
    private String mainHandle;
    private OwnExceptionHandler exceptionHandler;
    private ScreenShooter shooter;
    private WebEventListener listener;
    private Alert alert;
    private BrowserDriverKeeper keeper = new BrowserDriverKeeper();

    public Tellur(WebDriver driver) {
        init(driver);
    }

    public Tellur() {
        WebDriver  driver = keeper.getDriver(Thread.currentThread().getName(),"firefox");
        init(driver);
    }

    public Tellur(String browserType) {
        WebDriver  driver = keeper.getDriver(Thread.currentThread().getName(),browserType);
        init(driver);
    }

    private void init(WebDriver driver){
        this.webDriver = new EventFiringWebDriver(driver);
        listener = new WebEventListener(false);
        shooter = new ScreenShooter(webDriver,System.getProperty("user.dir")+"/Screenshots/");
        webDriver.register(listener);
        listener.setShooter(shooter);
    }

    public void setTimeToWait(int timeToWait) {
        this.timeToWait = timeToWait;
    }

    public void setOwnUnhandledExceptionHandler(){
        exceptionHandler = new OwnExceptionHandler(webDriver, true, false);
        Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
    }

    public void changeScreenshotFolder(String newFolder){
        exceptionHandler.setUserDir(newFolder);
    }

    public void makeScreenshot(String name) {
        shooter.getScreenShot(name);
    }

    public void goToPage(String name) {
        webDriver.get(name);
    }

    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    public boolean isPageURLContains(String text) {
        return getCurrentUrl().contains(text);
    }

    public WebElement getElement(By locator) {
        return webDriver.findElement(locator);
    }

    public boolean isElementPresent(By locator) { //проверка наличия элемента быстрый и без исключений
        webDriver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //ставим 0 для быстроты поиска
        List<WebElement> allElementsByLocator = getListByLocator(locator);
        webDriver.manage().timeouts().implicitlyWait(timeToWait, TimeUnit.SECONDS); //возвращаем нужное время
        return allElementsByLocator.size() > 0;
    }

    public boolean typeTextToInput(By locator, String text) {
        try {
            WebElement webElement = getElement(locator);
            webElement.clear();
            webElement.sendKeys(text);
        } catch (WebDriverException e) {
            return false;
        }
        return true;
    }

    public boolean typeTextToInput(WebElement webElement, String text) {
        try {
            webElement.clear();
            webElement.sendKeys(text);
        } catch (WebDriverException e) {
            return false;
        }
        return true;
    }

    public boolean clickElement(By locator) {
        try {
            getElement(locator).click();
        } catch (WebDriverException e) {
            return false;
        }
        return true;
    }

    public boolean clickElement(WebElement webElement) {
        try {
            webElement.click();
        } catch (WebDriverException e) {
            return false;
        }
        return true;
    }

    public boolean waitUntilOneWindowStay(int time) {
        boolean flag = false;
        while (time * 4 > 0) {
            Set<String> allWindowHandles = webDriver.getWindowHandles();
            if (allWindowHandles.size() <= 1) {
                flag = true;
                break;
            }
            time--;
            pause();
        }
        return flag;
    }

    public boolean waitUntilPageTitle(String title, int time) {
        WebDriverWait wait = new WebDriverWait(webDriver, time);
        try {
            wait.until(ExpectedConditions.titleIs(title));
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }

    public boolean waitUntilExist(By locator, int time) { //ожидание появления всех элементов класса
        return waitUntilConditions(locator, time, 0);
    }

    public boolean waitUntilVisible(By locator, int time) { //ожидание видимости элемента
        return waitUntilConditions(locator, time, 1);
    }

    public boolean waitUntilClickable(By locator, int time) { //ожидание возможности нажатия
        return waitUntilConditions(locator, time, 2);
    }

    public boolean waitUntilDissapear(By locator, int time) {
        return waitUntilConditions(locator, time, 3);
    }

    public boolean waitUntilConditions(By locator, int time, int type) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, time);
            if (type == 0) { //exist
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
            }
            if (type == 1) { //visible
                wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            }
            if (type == 2) { //clickable
                wait.until(ExpectedConditions.elementToBeClickable(locator));
            }
            if (type == 3) { //disappear
                wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            }
        } catch (TimeoutException ex) {
            return false;
        }
        return true;
    }

    public boolean waitUntilEnabled(WebElement element, int time) {
        int count = 0;
        while (count < time * 4) {
            String parameter = element.getAttribute("enabled");
            if (parameter != null) {
                return true;
            }
            pause();
            count++;
        }
        return false;
    }

    private boolean waitForNewWindowAndSwitchToIt(WebDriver driver) { // проверка что есть новое окно и переключение в него
        mainHandle = driver.getWindowHandle();
        String newWindowHandle = null;
        Set<String> allWindowHandles = driver.getWindowHandles();
        for (int i = 0; i < 10; i++) {
            if (allWindowHandles.size() > 1) {
                for (String allHandlers : allWindowHandles) {
                    if (!allHandlers.equals(mainHandle))
                        newWindowHandle = allHandlers;
                }
                driver.switchTo().window(newWindowHandle);
                break;
            } else {
                pause(1);
            }
        }
        if (mainHandle.equals(newWindowHandle)) {
            return false;
        }
        return true;
    }

    public void waitWhileSwitchingToNewWndow() { //ожидание переключения в новое окно
        waitForNewWindowAndSwitchToIt(webDriver);
    }

    public void switchToMainWindow() {
        webDriver.switchTo().window(mainHandle);
    }

    public void pause() {
        pause(0);
    }

    public void pause(int timeInSeconds) {
        try {
            if (timeInSeconds > 0) {
                Thread.sleep(timeInSeconds * 1000);
            } else {
                Thread.sleep(250);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<WebElement> getListByLocator(By locator) {
        return webDriver.findElements(locator);
    }

    public boolean contextClick(WebElement element) {
        return actionClick(element, "context");
    }

    public boolean doubleClick(WebElement element) {
        return actionClick(element, "double");
    }

    private boolean actionClick(WebElement element, String type) {
        Actions action;
        try {
            if (type.equals("double")) {
                action = new Actions(webDriver).doubleClick(element);
            } else {
                action = new Actions(webDriver).contextClick(element);
            }
            action.build().perform();
        } catch (WebDriverException e) {
            return false;
        }
        return true;
    }

    public void waitForJQueryEnds() {
        while ((Boolean) ((JavascriptExecutor) webDriver).executeScript("return jQuery.active!=0")) {
              //empty body or you can do something while waiting
        }
    }

    public boolean isJQueryOnThisPage() {
        return ((JavascriptExecutor) webDriver).executeScript("return (window.jQuery)") != null;
    }

    public void quit() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    public WebElement getElementByWordInClassName(String word){
        return getElement(By.xpath("//*[contains(@class,'"+word+"')]"));
    }

    public WebElement getElementByTagAndWordInClassName(String tag, String word){
        return getElement(By.xpath("//"+tag+"[contains(@class,'"+word+"')]"));
    }

    public WebElement getElementByText(String text) {
        return getElement(By.xpath("//*[contains(text(),'"+text+"')]"));
    }

    public WebElement getElementByTagAndText(String tag, String text) {
        return getElement(By.xpath("//"+tag+"[contains(text(),'"+text+"')]"));
    }

    public boolean isAlertPresent() { //проверка на наличие всплывающих окон
        try {
            alert = webDriver.switchTo().alert();
        } catch (NoAlertPresentException ex) {
            alert = null;
            return false;
        }
        return true;
    }

    public void acceptAllAlerts() { //говорим да всем всплывающим предупреждениям
        while (isAlertPresent()) {
            alert.accept();
        }
    }

    public void acceptAlert(){
        if (alert!=null) {
            alert.accept();
        }
        else {
            if (isAlertPresent()) {
                alert.accept();
            }
        }
        alert = null;
    }

    public void declineAlert(){
        if (alert!=null) {
            alert.dismiss();
        }
        else {
            if (isAlertPresent()) {
                alert.dismiss();
            }
        }
        alert = null;
    }

}
