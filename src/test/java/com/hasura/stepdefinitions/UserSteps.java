package com.hasura.stepdefinitions;

import com.hasura.apirouting.BaseRouteCall;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When; // Changed from And to When for better BDD flow

import static com.hasura.config.global.APIsGlobalConfigs.GRAPHQL_USERS_MODULE;

/**
 * Step definitions specific to GraphQL User and user-initiated Todo creation operations.
 */
public class UserSteps {

    @Given("I set the limit to {int}")
    public void i_set_the_limit_to(int limit) {
        com.hasura.modules.GraphQLUsersModule.dynamicLimit = limit;
    }

    @Given("I set the todo title to {string} and public status to {string}")
    public void i_set_the_todo_title_to_and_public_status(String title, String isPublic) {
        com.hasura.modules.GraphQLUsersModule.dynamicTodoTitle = title;
        com.hasura.modules.GraphQLUsersModule.dynamicTodoIsPublic = Boolean.parseBoolean(isPublic);
    }

    @When("User calls GraphQL Users module with scenario {string}")
    public void user_calls_graphql_users_module_with_scenario(String scenarioName) {
        // Set context for common steps
        CommonGraphQLSteps.currentFeatureModuleName = GRAPHQL_USERS_MODULE;
        CommonGraphQLSteps.currentFeatureScenarioName = scenarioName;

        // Reset global configurations before each scenario execution
        com.hasura.config.global.APIsGlobalConfigs.HTTP_STATUS_CODE = -200;

        System.out.println("GraphQL Module Name: " + GRAPHQL_USERS_MODULE + "\nScenario Name: " + scenarioName);
        BaseRouteCall.routeGraphQLCallWithModuleAndScenarioName(GRAPHQL_USERS_MODULE, scenarioName);
    }
}