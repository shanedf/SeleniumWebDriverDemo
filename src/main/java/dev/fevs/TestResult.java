package dev.fevs;

public class TestResult {

    private String testName;
    private String testDescription;
    private Boolean testPassed;
    private Exception testExceptions;

    public TestResult(String testName, String testDescription) {
        this.testName = testName;
        this.testDescription = testDescription;
        this.testPassed = null;
    }

    public void setTestResult(boolean testParameter) {
        this.testPassed = testParameter;
    }

    public void testPassed() {
        this.testPassed = true;
    }

    public void testFailed() {
        this.testPassed = false;
    }

    public void testExceptions(Exception ex) {
        this.testExceptions = ex;
    }

    public String name() {
        return this.testName;
    }

    public boolean result() {
        return this.testPassed;
    }

}
