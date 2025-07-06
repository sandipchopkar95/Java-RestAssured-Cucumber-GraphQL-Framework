package com.hasura.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features", // Point to the directory containing all feature files
        glue = {"com.hasura.stepdefinitions"},    // Package where all step definitions reside
        plugin = {"pretty", "html:target/cucumber-reports.html"},
        // tags = "@GraphQL", // Example: run all scenarios tagged with @GraphQL
        monochrome = true
)
public class TestRunner {
}