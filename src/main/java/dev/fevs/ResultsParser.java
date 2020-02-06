package dev.fevs;

import java.util.*;
import java.util.stream.Collectors;

public class ResultsParser {

    static final String ANSI_RESET = "\u001B[0m";
    static final String ANSI_GREEN = "\u001B[32m";
    static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    private static volatile ResultsParser instance = null;
    private static volatile Map<String, Map<String, TestResult>> testResults = null;

    private ResultsParser() {
        if (testResults != null) {
            throw new RuntimeException("Use getParser() method to create");
        }
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to create");
        }
    }

    static void parseResults(Map<String, Map<String, TestResult>> results) {
        testResults = results;
    }

    public static void showSummary() {
        Set<String> platforms = testResults.keySet();
        Set<String> uniqueTestKeys = testResults.values().stream().map(Map::keySet).flatMap(Set::stream).collect(Collectors.toSet());
        if (!platforms.isEmpty()) {
            final String[] tableKey = new String[]{"v TEST", "PLATFORM >"};
            int maxTestKeyLength = Math.max(Collections.max(uniqueTestKeys, Comparator.comparing(String::length)).length(), 19);

            int[] colWidths = platforms.stream().mapToInt(String::length).toArray();
            int[] columnWidths = new int[colWidths.length + 1];
            columnWidths[0] = maxTestKeyLength;
            System.arraycopy(colWidths, 0, columnWidths, 1, colWidths.length);
            String testColumnFormat = "%n| %-" + maxTestKeyLength + "s |";
            drawSeparatorLine(columnWidths);
            System.out.format(testColumnFormat, tableKey);
            platforms.forEach(plt -> {
                System.out.format(" %-" + plt.length() + "s |", plt);
            });
            System.out.format("%n");
            drawSeparatorLine(columnWidths);
            uniqueTestKeys.forEach(key -> {
                System.out.format(testColumnFormat, key);
                platforms.forEach(plt -> {
                    System.out.format(" %-" + plt.length() + "s |", formatResult(testResults.get(plt).get(key).result(), plt.length()));
                });
                System.out.format("%n");
                drawSeparatorLine(columnWidths);
            });
        } else {
            System.out.println(ANSI_RED_BACKGROUND + "The test suites returned no results!" + ANSI_RESET);
        }
    }

    static void drawSeparatorLine(int[] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+" + "-".repeat(width + 2));
        }
        System.out.print("+");
    }

    static String formatResult(boolean passResult, int fieldWidth) {
        StringBuilder sb = new StringBuilder(fieldWidth);
        sb.append(" ".repeat(Math.max(0, (fieldWidth - 6) / 2)));
        int padLength = sb.length();
        if (passResult) {
            sb.append(ANSI_GREEN + "PASSED" + ANSI_RESET);
        } else {
            sb.append(ANSI_RED_BACKGROUND + "FAILED" + ANSI_RESET);
        }
        sb.append(" ".repeat(Math.max(0, fieldWidth - (padLength + 6))));
        return sb.toString();
    }

}
