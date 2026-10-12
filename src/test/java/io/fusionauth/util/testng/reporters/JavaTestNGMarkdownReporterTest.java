/*
 * Copyright (c) 2025, Inversoft Inc., All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package io.fusionauth.util.testng.reporters;

import io.fusionauth.util.testng.reporters.testclasses.GroupedSuite;
import io.fusionauth.util.testng.reporters.testclasses.Suite1;
import io.fusionauth.util.testng.reporters.testclasses.Suite2;
import io.fusionauth.util.testng.reporters.testclasses.anotherpkg.Suite3;
import io.fusionauth.util.testng.reporters.testclasses.anotherpkg.Suite4;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.AssertJUnit.assertEquals;

public class JavaTestNGMarkdownReporterTest {
    private final File outputFileName = new File("my_output.md");

    private TestNG ng;

    private Path outputPath;

    @Test
    void multiple_suites() throws Exception {
        // arrange
        setupReporter("Multiple Suites Sample", 10);
        ng.setTestClasses(new Class[]{Suite1.class, Suite2.class, Suite3.class, Suite4.class});

        // act
        ng.run();

        // assert
        assertOutput("""
                # Multiple Suites Sample
                | Pass :white_check_mark: | Fail :x: | Skipped :heavy_minus_sign: |
                | ---- | ---- | ------- |
                |8|9|4|
                
                ## Failure List
                | Package | Test Class | Test Name |
                | ------- | ---------- | --------- |
                | io.fusionauth.util.testng.reporters.testclasses | Suite1 | a_failing_parameterized_test[bar] |
                |  | Suite1 | a_failing_parameterized_test[foo] |
                |  | Suite1 | failing_test |
                |  | Suite1$Nested | failing_nested_test |
                |  | Suite2 | failing_test |
                |  | Suite2 | failing_test_long_output |
                |  | Suite2 | failing_test_really_long_output |
                | io.fusionauth.util.testng.reporters.testclasses.anotherpkg | Suite3 | failing_test2 |
                |  | Suite4 | failing_test2 |
                
                ## Failure Details
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1#a_failing_parameterized_test[bar]`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.Suite1.a_failing_parameterized_test(Suite1.java:46)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1#a_failing_parameterized_test[foo]`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.Suite1.a_failing_parameterized_test(Suite1.java:46)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1#failing_test`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                \tat io.fusionauth.util.testng.reporters.testclasses.Suite1.failing_test(Suite1.java:60)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1$Nested#failing_nested_test`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.Suite1$Nested.failing_nested_test(Suite1.java:40)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite2#failing_test`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                \tat io.fusionauth.util.testng.reporters.testclasses.Suite2.failing_test(Suite2.java:43)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite2#failing_test_long_output`
                
                <details><summary>Show failure output...</summary>
                
                ```
                java.lang.AssertionError: line 0
                line 1
                line 2
                line 3
                line 4
                line 5
                line 6
                line 7
                line 8
                line 9
                line 10
                line 11
                line 12
                line 13
                line 14
                line 15
                line 16
                line 17
                line 18
                line 19
                \tat io.fusionauth.util.testng.reporters.testclasses.Suite2.failing_test_long_output(Suite2.java:51)
                ```
                </details>
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite2#failing_test_really_long_output`
                
                <details><summary>Show failure output...</summary>
                
                ```
                java.lang.AssertionError: line 0
                line 1
                line 2
                line 3
                line 4
                line 5
                line 6
                line 7
                line 8
                line 9
                line 10
                line 11
                line 12
                line 13
                line 14
                line 15
                line 16
                line 17
                line 18
                line 19
                line 20
                line 21
                line 22
                line 23
                line 24
                line 25
                line 26
                line 27
                line 28
                line 29
                line 30
                line 31
                line 32
                line 33
                line 34
                line 35
                line 36
                line 37
                line 38
                line 39
                line 40
                line 41
                line 42
                line 43
                line 44
                line 45
                line 46
                line 47
                line 48
                line 49
                ... (truncated)
                ```
                </details>
                
                ### `io.fusionauth.util.testng.reporters.testclasses.anotherpkg.Suite3#failing_test2`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                \tat io.fusionauth.util.testng.reporters.testclasses.anotherpkg.Suite3.failing_test2(Suite3.java:39)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.anotherpkg.Suite4#failing_test2`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                \tat io.fusionauth.util.testng.reporters.testclasses.anotherpkg.Suite4.failing_test2(Suite4.java:39)
                ```
                """);
    }

    @Test
    void no_tests() throws Exception {
        // arrange
        setupReporter("No Tests Sample", 10);
        // need a valid class in here, just one with no tests
        ng.setTestClasses(new Class[]{String.class});

        // act
        ng.run();

        // assert
        assertOutput("""
                # No Tests Sample
                | Pass :white_check_mark: | Fail :x: | Skipped :heavy_minus_sign: |
                | ---- | ---- | ------- |
                |0|0|0|
                """);
    }

    @Test
    void too_many_fails() throws Exception {
        setupReporter("Too Many Fails Sample", 1);
        ng.setTestClasses(new Class[]{Suite1.class, Suite2.class, Suite3.class, Suite4.class});

        // act
        ng.run();

        // assert
        assertOutput("""
                # Too Many Fails Sample
                | Pass :white_check_mark: | Fail :x: | Skipped :heavy_minus_sign: |
                | ---- | ---- | ------- |
                |8|9|4|
                
                ## Failure List
                | Package | Test Class | Test Name |
                | ------- | ---------- | --------- |
                | io.fusionauth.util.testng.reporters.testclasses | Suite1 | a_failing_parameterized_test[bar] |
                |  | Suite1 | a_failing_parameterized_test[foo] |
                |  | Suite1 | failing_test |
                |  | Suite1$Nested | failing_nested_test |
                |  | Suite2 | failing_test |
                |  | Suite2 | failing_test_long_output |
                |  | Suite2 | failing_test_really_long_output |
                | io.fusionauth.util.testng.reporters.testclasses.anotherpkg | Suite3 | failing_test2 |
                |  | Suite4 | failing_test2 |
                
                ## Failure Details
                
                (only showing first 1 failures due to configured limit)
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1#a_failing_parameterized_test[bar]`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.Suite1.a_failing_parameterized_test(Suite1.java:46)
                ```
                """);
    }

    void assertOutput(String expected) throws IOException {
        String withoutStackTraceVariances = Files.readString(outputPath)
                .replaceAll("(?sm)\\u0009at java.base\\/java.util.ArrayList.*?(```|</details>)",
                        "```");

        assertEquals(expected, withoutStackTraceVariances);
    }

    @Test
    public void multiple_groups() throws Exception {
        // arrange
        setupReporter("Same Suite Sample", 10);
        ng.setTestClasses(new Class[]{GroupedSuite.class});
        ng.setGroups("group1");

        // act
        ng.run();
        // ng seems to be sticky to groups, so to run a different group, reinstantiate
        var tla = new TestListenerAdapter();
        ng = new TestNG();
        ng.addListener(tla);
        setupReporter("Same Suite Sample", 10);
        ng.setTestClasses(new Class[]{GroupedSuite.class});
        ng.setGroups("group2");
        ng.run();

        // assert
        assertOutput("""
                # Same Suite Sample
                ## Test Groups: group1
                | Pass :white_check_mark: | Fail :x: | Skipped :heavy_minus_sign: |
                | ---- | ---- | ------- |
                |0|1|0|
                
                ## Failure List
                | Package | Test Class | Test Name |
                | ------- | ---------- | --------- |
                | io.fusionauth.util.testng.reporters.testclasses | GroupedSuite | failing_test_group1 |
                
                ## Failure Details
                
                ### `io.fusionauth.util.testng.reporters.testclasses.GroupedSuite#failing_test_group1`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.GroupedSuite.failing_test_group1(GroupedSuite.java:9)
                ```
                ## Test Groups: group2
                | Pass :white_check_mark: | Fail :x: | Skipped :heavy_minus_sign: |
                | ---- | ---- | ------- |
                |0|1|0|
                
                ## Failure List
                | Package | Test Class | Test Name |
                | ------- | ---------- | --------- |
                | io.fusionauth.util.testng.reporters.testclasses | GroupedSuite | failing_test_group2 |
                
                ## Failure Details
                
                ### `io.fusionauth.util.testng.reporters.testclasses.GroupedSuite#failing_test_group2`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.GroupedSuite.failing_test_group2(GroupedSuite.java:14)
                ```
                """);
    }

    @Test
    public void multiple_groups_exclude() throws Exception {
        // arrange
        setupReporter("Same Suite Sample", 10);
        ng.setTestClasses(new Class[]{GroupedSuite.class});
        ng.setGroups("group1");
        ng.setExcludedGroups("group2");

        // act
        ng.run();

        // assert
        assertOutput("""
                # Same Suite Sample
                ## Test Groups: group1, excluded: group2
                | Pass :white_check_mark: | Fail :x: | Skipped :heavy_minus_sign: |
                | ---- | ---- | ------- |
                |0|1|0|
                
                ## Failure List
                | Package | Test Class | Test Name |
                | ------- | ---------- | --------- |
                | io.fusionauth.util.testng.reporters.testclasses | GroupedSuite | failing_test_group1 |
                
                ## Failure Details
                
                ### `io.fusionauth.util.testng.reporters.testclasses.GroupedSuite#failing_test_group1`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.GroupedSuite.failing_test_group1(GroupedSuite.java:9)
                ```
                """);
    }

    @Test
    void tests_same_suite() throws IOException {
        // arrange
        setupReporter("Same Suite Sample", 10);
        ng.setTestClasses(new Class[]{Suite1.class});

        // act
        ng.run();

        // assert
        assertOutput("""
                # Same Suite Sample
                | Pass :white_check_mark: | Fail :x: | Skipped :heavy_minus_sign: |
                | ---- | ---- | ------- |
                |2|4|1|
                
                ## Failure List
                | Package | Test Class | Test Name |
                | ------- | ---------- | --------- |
                | io.fusionauth.util.testng.reporters.testclasses | Suite1 | a_failing_parameterized_test[bar] |
                |  | Suite1 | a_failing_parameterized_test[foo] |
                |  | Suite1 | failing_test |
                |  | Suite1$Nested | failing_nested_test |
                
                ## Failure Details
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1#a_failing_parameterized_test[bar]`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.Suite1.a_failing_parameterized_test(Suite1.java:46)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1#a_failing_parameterized_test[foo]`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.Suite1.a_failing_parameterized_test(Suite1.java:46)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1#failing_test`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                \tat io.fusionauth.util.testng.reporters.testclasses.Suite1.failing_test(Suite1.java:60)
                ```
                
                ### `io.fusionauth.util.testng.reporters.testclasses.Suite1$Nested#failing_nested_test`
                
                ```
                java.lang.AssertionError: expected [true] but found [false]
                	at io.fusionauth.util.testng.reporters.testclasses.Suite1$Nested.failing_nested_test(Suite1.java:40)
                ```
                """);
    }

    @BeforeMethod
    void setupAndCleanup() throws IOException {
        var tla = new TestListenerAdapter();
        ng = new TestNG();
        ng.addListener(tla);
        outputPath = Paths.get(ng.getOutputDirectory(), outputFileName.getName());
        Files.deleteIfExists(outputPath);
    }

    @AfterMethod
    void publishToGha() throws IOException {
        // GHA will render this on the summary page, provides a good "test"
        String githubStepSummaryFileName = System.getenv("GITHUB_STEP_SUMMARY");

        File ourOutputFile = outputPath.toFile();
        if (ourOutputFile.exists() && githubStepSummaryFileName != null) {
            Path githubSummaryFilePath = new File(githubStepSummaryFileName).toPath();

            // append to the file
            String ourOutput = Files.readString(outputPath);
            String newContents = Files.readString(githubSummaryFilePath) + ourOutput;
            Files.writeString(githubSummaryFilePath, newContents);
        }
    }

    void setupReporter(String reportName,
                       int failureLimit) {
        ng.addListener(new JavaTestNGMarkdownReporter(outputFileName, reportName, failureLimit));
    }
}
