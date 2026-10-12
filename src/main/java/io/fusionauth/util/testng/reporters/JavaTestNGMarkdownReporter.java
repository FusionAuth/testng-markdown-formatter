package io.fusionauth.util.testng.reporters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.testng.IReporter;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;

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

/**
 * Generates a markdown report for GitHub Actions style reports
 */
public class JavaTestNGMarkdownReporter implements IReporter {
  private final File outputFileName;

  private final String testTitle;

  private final int failurelimit;

  public JavaTestNGMarkdownReporter() {
    this(new File(Optional.ofNullable(System.getenv("testng_markdown_output_path"))
                          .orElse("test_report.md")),
        Optional.ofNullable(System.getenv("testng_markdown_report_name"))
                .orElse("Test Results"),
        Integer.parseInt(Optional.ofNullable(System.getenv("testng_markdown_test_failure_limit"))
                                 .orElse("10")));
  }

  JavaTestNGMarkdownReporter(File outputFileName, String testTitle, int failureLimit) {
    this.outputFileName = outputFileName;
    this.testTitle = testTitle;
    this.failurelimit = failureLimit;
  }

  @Override
  public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
    File outputFile = new File(outputDirectory, outputFileName.toString());
    final StringBuilder result = new StringBuilder();

    // only want to put test title in a fresh file
    if (!outputFile.exists()) {
      result.append("# %s\n".formatted(testTitle));
    }

    List<ISuiteResult> suiteResults = suites.stream().flatMap(s -> s.getResults().values().stream())
                                            .toList();
    ITestContext testContext = suiteResults.get(0).getTestContext();
    String groupHeader = null;
    if (testContext.getIncludedGroups().length > 0) {
      groupHeader = String.join(", ", testContext.getIncludedGroups());
    }
    if (testContext.getExcludedGroups().length > 0) {
      String flatExcludes = "excluded: " + String.join(", ", testContext.getExcludedGroups());
      groupHeader = Optional.ofNullable(groupHeader)
                            .map(existing -> existing + ", " + flatExcludes)
                            .orElse(flatExcludes);
    }
    if (groupHeader != null) {
      result.append("## Test Groups: %s\n".formatted(groupHeader));
    }

    result.append("| Pass :white_check_mark: | Fail :x: | Skipped :heavy_minus_sign: |\n");
    result.append("| ---- | ---- | ------- |\n");

    Integer failedTests = getTotals(suiteResults, ITestContext::getFailedTests);

    result.append(String.format("|%d|%d|%d|\n",
        getTotals(suiteResults, ITestContext::getPassedTests),
        failedTests,
        getTotals(suiteResults, ITestContext::getSkippedTests)));

    if (failedTests > 0) {
      addFailureInfo(result, suiteResults);
    }

    try {
      Files.write(outputFile.toPath(),
          result.toString().getBytes(),
          // if we have multiple group runs, don't want to erase the file
          StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void addFailureInfo(StringBuilder result, List<ISuiteResult> suiteResults) {
    result.append("\n");
    result.append("## Failure List\n");
    result.append("| Package | Test Class | Test Name |\n");
    result.append("| ------- | ---------- | --------- |\n");

    List<TestResult> allResults = suiteResults.stream()
                                              .flatMap(sr -> sr.getTestContext().getFailedTests().getAllResults().stream())
                                              .map(TestResult::fromTestNg)
                                              .sorted(Comparator.<TestResult, String>comparing(tr -> tr.packageName)
                                                                .thenComparing(tr -> tr.testClassName)
                                                                .thenComparing(tr -> tr.testName))
                                              .toList();

    Map<String, List<TestResult>> byPackage = allResults.stream()
                                                        .collect(Collectors.groupingBy(tr -> tr.packageName));

    byPackage.forEach((key, value) ->
        IntStream.range(0, value.size()).forEach(index -> {
          TestResult tr = value.get(index);

          result.append(String.format("| %s | %s | %s |\n",
              // package name only on 1st row
              index == 0 ? key : "",
              tr.testClassName,
              tr.testName));
        }));

    result.append("\n## Failure Details\n");

    if (allResults.size() > failurelimit) {
      result.append("\n(only showing first %d failures due to configured limit)\n".formatted(failurelimit));
    }

    IntStream.range(0, allResults.size()).forEach(index -> {
      if (index >= failurelimit) {
        return;
      }

      TestResult tr = allResults.get(index);
      result.append(String.format("\n### `%s.%s#%s`\n\n", tr.packageName, tr.testClassName, tr.testName));
      // testng utility that filters out irrelevant parts of stack trace
      String failureOutput = Utils.shortStackTrace(tr.testFailure, false);

      // if failure output is long, use a collapsible section.
      // https://docs.github.com/en/get-started/writing-on-github/working-with-advanced-formatting/organizing-information-with-collapsed-sections
      if (failureOutput.lines().count() > 10) {
        if (failureOutput.lines().count() > 50) {
          // if the stack trace is really long, only show the first 50 lines
          failureOutput = failureOutput.lines()
                                       .limit(50)
                                       .collect(Collectors.joining("\n")) + "\n... (truncated)";
        }
        result.append("<details><summary>Show failure output...</summary>\n\n");
        result.append("```\n");
        result.append(failureOutput);
        result.append("\n```\n");
        result.append("</details>\n");
      } else {
        result.append("```\n");
        result.append(failureOutput);
        result.append("\n```\n");
      }
    });
  }

  private record TestResult(String packageName, String testClassName, String testName, Throwable testFailure) {
    public static TestResult fromTestNg(ITestResult tr) {
      String params = Arrays.stream(tr.getParameters())
                            .map(p -> {
                              try {
                                // in some edge cases, p can be null
                                return Optional.ofNullable(p)
                                               .map(Object::toString)
                                               .orElse("(null)");
                              } catch (Exception e) {
                                // in case we have toString problems
                                return p.getClass().getName() + "@" + System.identityHashCode(p);
                              }
                            })
                            .collect(Collectors.joining(", "));

      String testName = tr.getName();

      if (!params.isEmpty()) {
        testName += "[" + params + "]";
      }

      Class<?> realTestClass = tr.getTestClass().getRealClass();
      String simpleClassNameWithNested = realTestClass.getName().replace(realTestClass.getPackageName() + ".", "");

      return new TestResult(realTestClass.getPackageName(),
          simpleClassNameWithNested,
          testName,
          tr.getThrowable());
    }
  }

  private static Integer getTotals(List<ISuiteResult> suiteResults,
                                   Function<ITestContext, IResultMap> selector) {

    return suiteResults.stream()
                       .reduce(0,
                           (total, suiteResult) -> selector.apply(suiteResult.getTestContext()).size() + total,
                           Integer::sum);
  }
}
