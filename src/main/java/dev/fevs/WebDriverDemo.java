package dev.fevs;

import org.javatuples.Pair;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class WebDriverDemo {
    public static void main(String[] args) {

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<AngularJsTestSuite> testSuites = new ArrayList<>();

        testSuites.add(new AngularJsTestSuite(new ChromeDriver()));
        testSuites.add(new AngularJsTestSuite(new EdgeDriver()));
        testSuites.add(new AngularJsTestSuite(new FirefoxDriver()));

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
                showResult(resultMap);
        }

    }

    // https://stackoverflow.com/questions/15215326/how-can-i-create-table-using-ascii-in-a-console
    static void showResult(Map<String, Map<String, TestResult>> testResults) {
        final String H_BORDER = "-";
        final String V_BORDER = "|";
        final String SPACER = " ";
        final String ANSI_RESET = "\u001B[0m";
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RED_BACKGROUND = "\u001B[41m";
        Set<String> platforms = testResults.keySet();
        List<String> uniqueTestKeys = testResults.values().stream().map(Map::keySet).flatMap(Set::stream).collect(Collectors.toList());
        int maxTestKeyLength = Collections.max(uniqueTestKeys, Comparator.comparing(String::length)).length();
        System.out.println(maxTestKeyLength);
        if (!platforms.isEmpty()) {
            System.out.println(H_BORDER.repeat(maxTestKeyLength + 2) + (H_BORDER.repeat(11)).repeat(platforms.size()));
            System.out.println(V_BORDER + SPACER.repeat(maxTestKeyLength) + V_BORDER + (SPACER.repeat(10) + V_BORDER).repeat(platforms.size()));
            /*testResults.getValue1().forEach(((testKey, testResult) -> {
                System.out.println(testKey + " // " + (testResult.result() ? (ANSI_GREEN + "PASSED") : (ANSI_RED_BACKGROUND + "FAILED")) + ANSI_RESET);
            }));*/
        } else {
            System.out.println(ANSI_RED_BACKGROUND + "One of the test suites returned no results!" + ANSI_RESET);
        }
    }
}
