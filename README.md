# What it is

A simple markdown reporter/formatter for TestNG.

# What it does

* Formats test output in markdown
* Includes tests that failed by package
* If the failure count is less than a configurable amount (10 by default, the stack trace/details of the failure are shown)

# Using

1. Add the dependency to your Savant file in the test-runtime group
   ```
   dependency(id: "io.fusionauth:testng-markdown-formatter:1.0.0")
   ```
2. Configure the markdown reporter as a listener using the following testng argument:
   `-listener io.fusionauth.util.testng.reporters.JavaTestNGMarkdownReporter`

When you run your tests, the markdown will be in the TestNG report output directory as `test_report.md` by default.

# Customizing

Report filename - set the `testng_markdown_output_path` environment variable

Report header in the markdown - `testng_markdown_report_name`

Number of failures to show details of - `testng_markdown_test_failure_limit`

# Building

`sb clean test`
