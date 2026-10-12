package io.fusionauth.util.testng.reporters;


import org.testng.*;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaTestNGProgressReporter implements ITestListener, IMethodInterceptor {
    private final PrintStream output;

    private int totalTestCount;

    private final AtomicInteger currentTestIndex = new AtomicInteger();

    private final HashSet<ITestNGMethod> completedMethods = new HashSet<>();

    private int lastPrintedPercent = -1;

    public JavaTestNGProgressReporter() {
        this(System.out);
    }

    public JavaTestNGProgressReporter(PrintStream output) {
        this.output = output;
    }

    // interceptors can modify the total test count so the easiest way to get the total test count is to also be an interceptor
    @Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
        // This does not include the additional runs for data driven/parameterized tests. This cannot really be
        // determined in advanced because DataProvider methods are not evaluated until just before
        // the test method is invoked.
        totalTestCount = methods.size();
        currentTestIndex.set(0);
        lastPrintedPercent = 0;
        return methods;
    }

    public int getTotalTestCount() {
        return totalTestCount;
    }

    @Override
    public void onTestStart(ITestResult result) {
        // we can't estimate dataprovider test counts up front, but we can keep track of whether
        // we've already executed a single test method or not
        int currentIndex = currentTestIndex.get();
        if (completedMethods.add(result.getMethod())) {
            currentIndex = currentTestIndex.incrementAndGet();
        }

        int percent = (int) ((double) currentIndex / totalTestCount * 100);
        // don't want to print the same percentage update again
        if (percent % 5 == 0 && percent > lastPrintedPercent) {
            lastPrintedPercent = percent;
            output.printf("%d%% - %d failures/%d total\n",
                    percent,
                    result.getTestContext().getFailedTests().size(),
                    totalTestCount);
        }
    }
}
