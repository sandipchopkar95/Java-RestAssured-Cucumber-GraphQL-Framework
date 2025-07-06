package com.hasura.stepdefinitions;

import com.hasura.apirouting.BaseRouteCall;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When; // Changed from And to When for better BDD flow

import static com.hasura.config.global.APIsGlobalConfigs.GRAPHQL_TODOS_MODULE;

/**
 * Step definitions specific to GraphQL Todo operations.
 */
public class TodoSteps {

    @Given("I set the todo ID to {int}")
    public void i_set_the_todo_id_to(int todoId) {
        com.hasura.modules.GraphQLTodosModule.dynamicTodoId = todoId;
    }

    @When("User calls GraphQL Todos module with scenario {string}")
    public void user_calls_graphql_todos_module_with_scenario(String scenarioName) {
        // Set context for common steps
        CommonGraphQLSteps.currentFeatureModuleName = GRAPHQL_TODOS_MODULE;
        CommonGraphQLSteps.currentFeatureScenarioName = scenarioName;

        // Reset global configurations before each scenario execution
        com.hasura.config.global.APIsGlobalConfigs.HTTP_STATUS_CODE = -200;

        System.out.println("GraphQL Module Name: " + GRAPHQL_TODOS_MODULE + "\nScenario Name: " + scenarioName);
        BaseRouteCall.routeGraphQLCallWithModuleAndScenarioName(GRAPHQL_TODOS_MODULE, scenarioName);
    }
}