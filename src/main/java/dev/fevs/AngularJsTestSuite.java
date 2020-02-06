package dev.fevs;

import org.javatuples.Pair;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class AngularJsTestSuite implements Callable<Pair<String, Map<String, TestResult>>> {

    private WebDriver driver = null;
    private String platform;
    private Map<String, TestResult> testResults;

    final String baseUrl = "https://demoapps.feather-lopez.com/seleniumdemo/angularjs";

    public AngularJsTestSuite(WebDriver driver) {
        this.driver = driver;
        testResults = new HashMap<>();
        String[] clazzName = this.driver.getClass().getName().split("[.]");
        platform = clazzName[clazzName.length - 1].replace("Driver"," LOCAL").toUpperCase();
    }

    public AngularJsTestSuite(URL serverAddress, Capabilities targetPlatform) {
        this.driver = new RemoteWebDriver(serverAddress, targetPlatform);
        testResults = new HashMap<>();
        platform = targetPlatform.getBrowserName().toUpperCase() + " REMOTE";
    }

    @Override
    public Pair<String, Map<String, TestResult>> call() throws Exception {
        return runTestSuite();
    }

    private Pair<String, Map<String, TestResult>> runTestSuite() {
        checkBbcWebsite();
        successfulHomePageLoad();
        canNavigateToProducts();
        return Pair.with(platform, testResults);
    }

    private void successfulHomePageLoad() {
        String testName = "Successful HomePage Loading";
        TestResult res = new TestResult(testName, "Browser should successfully load page");
        try {
            driver.get(baseUrl);
            WebElement homeLink = driver.findElements(By.className("navbar-brand")).get(0);
            res.setTestResult(homeLink.getText().equalsIgnoreCase("Acme Product Management"));
            driver.close();
        } catch (WebDriverException ex) {
            res.testExceptions(ex);
            res.testFailed();
        }
        testResults.put(testName, res);
    }

    private void canNavigateToProducts() {
        String testName = "Successful Navigation to Products";
        TestResult res = new TestResult(testName, "Browser should successfully navigate to products page");
        try {
            driver.get(baseUrl);
            WebElement productsLink = driver.findElements(By.tagName("li")).get(0);
            if (productsLink.getText().equalsIgnoreCase("Product List")) {
                productsLink.click();
                WebElement showImagesButton = driver.findElements(By.className("btn-primary")).get(0);
                res.setTestResult(true);
            } else {
                System.out.println(productsLink.getText());
            }
            driver.close();
        } catch (WebDriverException ex) {
            res.testExceptions(ex);
            res.testFailed();
        }
        testResults.put(testName, res);
    }

    private void checkBbcWebsite() {
        try {
            driver.get("http://www.bbc.co.uk");
            WebElement signInLink = driver.findElement(By.id("idcta-link"));
            signInLink.click();
            WebElement username = driver.findElement(By.name("username"));
            username.sendKeys("test");
        } catch (Exception ex) {

        }
    }

}
