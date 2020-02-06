package dev.fevs;

import org.javatuples.Pair;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class WebDriverDemo {
    public static void main(String[] args) throws MalformedURLException {

        URL seleniumServer = new URL("http://localhost:4444/wd/hub");

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<AngularJsTestSuite> testSuites = new ArrayList<>();

        testSuites.add(new AngularJsTestSuite(new ChromeDriver()));
        testSuites.add(new AngularJsTestSuite(new SafariDriver()));
        testSuites.add(new AngularJsTestSuite(seleniumServer, new FirefoxOptions()));
        testSuites.add(new AngularJsTestSuite(seleniumServer, new ChromeOptions()));

        List<Future<Pair<String, Map<String, TestResult>>>> resultSets = null;

        try {
            resultSets = executorService.invokeAll(testSuites);
            executorService.shutdown();
            if(!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            e.printStackTrace();
        }

        if (resultSets != null) {
                Map<String, Map<String, TestResult>> resultMap =
                resultSets.stream()
                        .collect(Collectors.toMap(l -> {
                            try {
                                return l.get().getValue0();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }, l -> {
                            try {
                                return l.get().getValue1();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }));
                ResultsParser.parseResults(resultMap);
                ResultsParser.showSummary();
        }

    }


}
