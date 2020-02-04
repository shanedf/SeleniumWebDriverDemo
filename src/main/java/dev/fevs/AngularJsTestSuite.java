package dev.fevs;

import org.javatuples.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class AngularJsTestSuite implements Callable<Pair<String, Map<String, TestResult>>> {

    private WebDriver driver;
    private Map<String, TestResult> testResults;

    final String baseUrl = "https://demoapps.feather-lopez.com/seleniumdemo/angularjs";

    public AngularJsTestSuite(WebDriver driver) {
        this.driver = driver;
        testResults = new HashMap<>();
    }

    @Override
    public Pair<String, Map<String, TestResult>> call() throws Exception {
        return runTestSuite();
    }

    private Pair<String, Map<String, TestResult>> runTestSuite() {
        successfulHomePageLoad();
        String[] clazzName = this.driver.getClass().getName().split("[.]");
        return Pair.with(clazzName[clazzName.length - 1].replace("Driver",""), testResults);
    }

    private void successfulHomePageLoad() {
        String testName = "Successful Navigation";
        TestResult res = new TestResult(testName, "Browser should successfully load page");
        try {
            driver.get(baseUrl);
            WebElement homeLink = driver.findElement(By.className("navbar-brand"));
            res.setTestResult(homeLink.getText().equalsIgnoreCase("Acme Product Management"));
            driver.close();
        } catch (WebDriverException ex) {
            res.testExceptions(ex);
            res.testFailed();
        }
        testResults.put(testName, res);
    }

}
