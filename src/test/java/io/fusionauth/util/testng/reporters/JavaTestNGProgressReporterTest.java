package io.fusionauth.util.testng.reporters;

import io.fusionauth.util.testng.reporters.testclasses.Suite1;
import io.fusionauth.util.testng.reporters.testclasses.Suite2;
import io.fusionauth.util.testng.reporters.testclasses.anotherpkg.Suite3;
import io.fusionauth.util.testng.reporters.testclasses.anotherpkg.Suite4;
import org.testng.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class JavaTestNGProgressReporterTest {
    private TestNG ng;

    private ByteArrayOutputStream baos;

    @Test
    public void report_1_test() {
        // arrange

        // act
        TestResults result = unitStyleInvocation(1);

        // assert
        assertEquals(result.output(), "100% - 0 failures/1 total\n");
        assertEquals(result.testCount(), 1);
    }

    @Test
    public void report_2_tests() {
        // arrange

        // act
        TestResults result = unitStyleInvocation(2);

        // assert
        assertEquals(result.output(),
                "50% - 0 failures/2 total\n100% - 0 failures/2 total\n");
        assertEquals(result.testCount(), 2);
    }

    @Test
    public void report_3_tests() {
        // arrange

        // act
        TestResults result = unitStyleInvocation(3);

        // assert
        assertEquals(result.output(),
                "100% - 0 failures/3 total\n");
        assertEquals(result.testCount(), 3);
    }

    @Test
    public void report_160_tests() {
        // arrange

        // act
        TestResults result = unitStyleInvocation(160);

        // assert
        assertEquals(result.output(),
                """
                        5% - 0 failures/160 total
                        10% - 0 failures/160 total
                        15% - 0 failures/160 total
                        20% - 0 failures/160 total
                        25% - 0 failures/160 total
                        30% - 0 failures/160 total
                        35% - 0 failures/160 total
                        40% - 0 failures/160 total
                        45% - 0 failures/160 total
                        50% - 0 failures/160 total
                        55% - 0 failures/160 total
                        60% - 0 failures/160 total
                        65% - 0 failures/160 total
                        70% - 0 failures/160 total
                        75% - 0 failures/160 total
                        80% - 0 failures/160 total
                        85% - 0 failures/160 total
                        90% - 0 failures/160 total
                        95% - 0 failures/160 total
                        100% - 0 failures/160 total
                        """,
                "Output should match expected multiline string");
        assertEquals(result.testCount(), 160);
    }

    @Test
    public void report_more_than_estimated_methods() throws Exception {
        // Use case: Data provider test permutations will not be included in the up front count (they can't, see JavaTestNGProgressReporter for why)
        //           but when our current test index eclipses the estimated total test count, we should increment our
        //           total test count and continue to report progress

        // arrange
        baos = new ByteArrayOutputStream();

        JavaTestNGProgressReporter reporter = new JavaTestNGProgressReporter(new PrintStream(baos));
        // 1 method up front
        List<IMethodInstance> methods = IntStream.range(0, 1)
                .boxed()
                .map(index -> mock(IMethodInstance.class))
                .toList();

        // act
        reporter.intercept(methods, null);
        // "start" each test, with 2 more than estimated to simulate data provider
        ITestNGMethod mockMethod = mock(ITestNGMethod.class);
        for (int i = 0; i < methods.size() + 2; i++) {
            // new result, but use the same method
            ITestResult mockResult = mock(ITestResult.class);
            when(mockResult.getMethod()).thenReturn(mockMethod);
            ITestContext mockContext = mock(ITestContext.class);
            when(mockResult.getTestContext()).thenReturn(mockContext);
            IResultMap mockResultMap = mock(IResultMap.class);
            when(mockContext.getFailedTests()).thenReturn(mockResultMap);
            when(mockResultMap.size()).thenReturn(0);
            reporter.onTestStart(mockResult);
        }
        TestResults result = new TestResults(reporter.getTotalTestCount(), baos.toString());

        // assert
        assertEquals(result.testCount(),
                1,
                "We should not count data provider stuff");
        assertEquals(result.output(),
                "100% - 0 failures/1 total\n");
    }

    record TestResults(int testCount, String output) {
    }

    private TestResults unitStyleInvocation(int numberOfMethods) {
        baos = new ByteArrayOutputStream();

        JavaTestNGProgressReporter reporter = new JavaTestNGProgressReporter(new PrintStream(baos));
        List<IMethodInstance> methods = IntStream.range(0, numberOfMethods)
                .boxed()
                .map(index -> mock(IMethodInstance.class))
                .toList();

        reporter.intercept(methods, null);
        // "start" each test
        for (int i = 0; i < methods.size(); i++) {
            ITestResult mockResult = mock(ITestResult.class);
            ITestNGMethod mockMethod = mock(ITestNGMethod.class);
            when(mockResult.getMethod()).thenReturn(mockMethod);
            ITestContext mockContext = mock(ITestContext.class);
            when(mockResult.getTestContext()).thenReturn(mockContext);
            IResultMap mockResultMap = mock(IResultMap.class);
            when(mockContext.getFailedTests()).thenReturn(mockResultMap);
            when(mockResultMap.size()).thenReturn(0);
            reporter.onTestStart(mockResult);
        }

        return new TestResults(reporter.getTotalTestCount(), baos.toString());
    }

    @Test
    public void integration_test() {
        // Use case: typical case where we are the only listener

        // arrange
        setupReporter();
        // suite1 has 6 enabled test methods, 1 of them has a dataprovider w/ 2 permutations
        // suite2 has 6 enabled test methods (1 of them will skip when run)
        // suite3 has 4 enabled test methods
        // suite4 has 4 enabled test methods
        ng.setTestClasses(new Class[]{Suite1.class, Suite2.class, Suite3.class, Suite4.class});

        // act
        String output = runIt();

        // assert
        assertEquals(output, """
                        5% - 0 failures/20 total
                        10% - 2 failures/20 total
                        15% - 2 failures/20 total
                        20% - 2 failures/20 total
                        25% - 3 failures/20 total
                        30% - 3 failures/20 total
                        35% - 4 failures/20 total
                        40% - 4 failures/20 total
                        45% - 4 failures/20 total
                        50% - 5 failures/20 total
                        55% - 6 failures/20 total
                        60% - 7 failures/20 total
                        65% - 7 failures/20 total
                        70% - 7 failures/20 total
                        75% - 7 failures/20 total
                        80% - 8 failures/20 total
                        85% - 8 failures/20 total
                        90% - 8 failures/20 total
                        95% - 8 failures/20 total
                        100% - 9 failures/20 total
                        """,
                "Output should match expected multiline string");
    }

    @Test
    public void integration_test_interceptor() {
        // arrange
        ng.addListener((IMethodInterceptor) (methods, context) -> {
            BiConsumer<String, List<IMethodInstance>> printer = (String label, List<IMethodInstance> stuff) -> {
                List<String> names = stuff.stream().map(m -> m.getMethod().getMethodName()).toList();
                System.out.printf("intercepting, method list %s interception: %s\n", label, names);
            };
            printer.accept("before", methods);
            List<IMethodInstance> trimmed = methods.subList(0, methods.size() - 2);
            printer.accept("after", trimmed);
            return trimmed;
        });
        baos = new ByteArrayOutputStream();
        ng.addListener(new JavaTestNGProgressReporter(new PrintStream(baos)));
        ng.setTestClasses(new Class[]{Suite1.class});

        // act
        String output = runIt();

        // assert
        assertEquals(output, """
                        25% - 0 failures/4 total
                        50% - 2 failures/4 total
                        75% - 2 failures/4 total
                        100% - 2 failures/4 total
                        """,
                "Output should match expected multiline string");
    }

    String runIt() {
        ng.run();
        return baos.toString();
    }

    void setupReporter() {
        baos = new ByteArrayOutputStream();
        ng.addListener(new JavaTestNGProgressReporter(new PrintStream(baos)));
    }

    @BeforeMethod
    void setupAndCleanup() throws IOException {
        var tla = new TestListenerAdapter();
        ng = new TestNG();
        ng.addListener(tla);
    }
}
