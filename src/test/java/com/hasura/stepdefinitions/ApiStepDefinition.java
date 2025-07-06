package com.hasura.stepdefinitions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasura.apirouting.BaseRouteCall;
import com.hasura.config.graphql.GraphQLUsersConfig;
import com.hasura.modules.GraphQLUsersModule;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;

import static com.hasura.config.global.APIsGlobalConfigs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ApiStepDefinition {
    String featureModuleName;
    String featureScenarioName;

    private final ObjectMapper objectMapper = new ObjectMapper(); // Re-use ObjectMapper

    @Given("User calls GraphQL {string} with scenario {string}")
    public void user_calls_graphql_with_scenario(String moduleName, String scenarioName) {
        // Reset global configurations before each scenario execution
        HTTP_STATUS_CODE = -200;
        RESPONSE_BODY_STATUS_CODE = -1;
        this.featureModuleName = moduleName;
        this.featureScenarioName = scenarioName;

        System.out.println("GraphQL Module Name: " + moduleName + "\nScenario Name: " + scenarioName);
        BaseRouteCall.routeGraphQLCallWithModuleAndScenarioName(moduleName, scenarioName);
    }

    @Given("I set the limit to {int}")
    public void i_set_the_limit_to(int limit) {
        GraphQLUsersModule.dynamicLimit = limit;
    }

    @Given("I set the todo title to {string} and public status to {string}")
    public void i_set_the_todo_title_to_and_public_status(String title, String isPublic) {
        GraphQLUsersModule.dynamicTodoTitle = title;
        GraphQLUsersModule.dynamicTodoIsPublic = Boolean.getBoolean(isPublic);
    }

    @Then("http_status_code is {int}")
    public void http_status_codeIsHttpCode(int httpCode) {
        assertEquals(httpCode, HTTP_STATUS_CODE, "HTTP Status Code mismatch.");
    }

    @And("response_body_status_code is {int}")
    public void response_body_status_codeIsResponseBodyCode(int responseBodyCode) {
        assertEquals(responseBodyCode, RESPONSE_BODY_STATUS_CODE, "Response Body Status Code mismatch.");
    }

    @And("GraphQL response has no errors")
    public void graphql_response_has_no_errors() {
        // Choose the correct response object based on the current module/scenario
        if (featureModuleName.equals(GRAPHQL_USERS_MODULE) && featureScenarioName.startsWith("Fetch_Users")) {
            assertNotNull(GraphQLUsersModule.actualUsersResponse, "GraphQL Users response object is null.");
            assertTrue(GraphQLUsersModule.actualUsersResponse.getErrors() == null ||
                            GraphQLUsersModule.actualUsersResponse.getErrors().isEmpty(),
                    "GraphQL Users response contains errors: " + (GraphQLUsersModule.actualUsersResponse.getErrors() != null ?
                            GraphQLUsersModule.actualUsersResponse.getErrors().toString() : ""));
        } else if (featureModuleName.equals(GRAPHQL_USERS_MODULE) && featureScenarioName.equals(GraphQLUsersConfig.CREATE_TODO_SCENARIO)) {
            assertNotNull(GraphQLUsersModule.actualTodoMutationResponse, "GraphQL Todo Mutation response object is null.");
            assertTrue(GraphQLUsersModule.actualTodoMutationResponse.getErrors() == null ||
                            GraphQLUsersModule.actualTodoMutationResponse.getErrors().isEmpty(),
                    "GraphQL Todo Mutation response contains errors: " + (GraphQLUsersModule.actualTodoMutationResponse.getErrors() != null ?
                            GraphQLUsersModule.actualTodoMutationResponse.getErrors().toString() : ""));
        } else {
            throw new IllegalStateException("Unhandled GraphQL module/scenario for error validation: " + featureModuleName + "/" + featureScenarioName);
        }
        System.out.println("GraphQL response has no errors.");
    }


    @And("GraphQL response contains data for {string}")
    public void graphql_response_contains_data_for(String scenarioName) {
        try {
            JsonNode actualDataNode;
            if (scenarioName.equals(GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO) ||
                    scenarioName.equals(GraphQLUsersConfig.FETCH_USERS_ONLY_SCENARIO)) {
                assertNotNull(GraphQLUsersModule.actualUsersResponse, "Actual users response is null.");
                assertNotNull(GraphQLUsersModule.actualUsersResponse.getData(), "Users response 'data' field is null.");
                actualDataNode = objectMapper.valueToTree(GraphQLUsersModule.actualUsersResponse.getData());

                // Assertions for Users Query
                assertTrue(actualDataNode.has("users"), "'users' array not found in response data.");
                assertTrue(actualDataNode.get("users").isArray(), "'users' field is not an array.");
                // For dynamic limit, check the size
                if (scenarioName.equals(GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO)) {
                    assertTrue(actualDataNode.get("users").size() <= GraphQLUsersModule.dynamicLimit,
                            "Number of users returned (" + actualDataNode.get("users").size() +
                                    ") exceeds the specified limit (" + GraphQLUsersModule.dynamicLimit + ").");
                    // Further assertions can go here, e.g., check structure of each user/todo
                }
                // Example: Validate specific user fields
                if (actualDataNode.get("users").size() > 0) {
                    JsonNode firstUser = actualDataNode.get("users").get(0);
                    assertTrue(firstUser.has("id"), "User object missing 'id'.");
                    assertTrue(firstUser.has("name"), "User object missing 'name'.");
                    assertTrue(firstUser.has("todos"), "User object missing 'todos'.");
                    assertTrue(firstUser.get("todos").isArray(), "User todos is not an array.");
                }

            } else if (scenarioName.equals(GraphQLUsersConfig.CREATE_TODO_SCENARIO)) {
                assertNotNull(GraphQLUsersModule.actualTodoMutationResponse, "Actual todo mutation response is null.");
                assertNotNull(GraphQLUsersModule.actualTodoMutationResponse.getData(), "Todo mutation response 'data' field is null.");
                actualDataNode = objectMapper.valueToTree(GraphQLUsersModule.actualTodoMutationResponse.getData());

                assertTrue(actualDataNode.has("insert_todos"), "insert_todos field not found in data.");
                JsonNode insertTodosNode = actualDataNode.get("insert_todos");
                assertNotNull(insertTodosNode.get("returning"), "returning array not found for insert_todos.");
                assertTrue(insertTodosNode.get("returning").isArray(), "returning is not an array.");
                assertTrue(insertTodosNode.get("returning").size() > 0, "No todos returned after insertion.");

                JsonNode returnedTodo = insertTodosNode.get("returning").get(0);
                assertNotNull(GraphQLUsersModule.expectedTodoReturningData, "Expected todo returning data is null.");

                Map<String, Object> expectedDataMap = objectMapper.convertValue(GraphQLUsersModule.expectedTodoReturningData, Map.class);
                validateJsonResponse(returnedTodo, expectedDataMap);

            } else {
                throw new IllegalArgumentException("Unsupported GraphQL scenario for data validation: " + scenarioName);
            }
            System.out.println("GraphQL response 'data' matches expected for scenario: " + scenarioName);
        } catch (Exception e) {
            throw new RuntimeException("Error validating GraphQL response 'data' for scenario ["
                    + scenarioName + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to recursively validate a JSON Node against a map of expected values.
     * @param jsonResponse The actual JSON Node from the response.
     * @param expectedValues A map of key-value pairs representing the expected data.
     */
    private void validateJsonResponse(JsonNode jsonResponse, Map<String, Object> expectedValues) {
        for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
            String key = entry.getKey();
            Object expectedValue = entry.getValue();

            // Special handling for 'id' as it's generated by the server for new entities
            if (key.equals("id")) {
                assertTrue(jsonResponse.has(key), "Key '" + key + "' is missing in the response.");
                assertTrue(jsonResponse.get(key).asInt() > 0, "ID for '" + key + "' must be a positive integer.");
                continue; // Skip direct value comparison for ID
            }

            assertTrue(jsonResponse.has(key), "Key '" + key + "' is missing in the response.");
            JsonNode actualValueNode = jsonResponse.get(key);

            if (expectedValue instanceof Map) {
                assertTrue(actualValueNode.isObject(), "Key '" + key + "' should contain a JSON object.");
                validateJsonResponse(actualValueNode, (Map<String, Object>) expectedValue);
            } else if (expectedValue instanceof List) {
                // Handle nested lists, e.g., 'todos'
                assertTrue(actualValueNode.isArray(), "Key '" + key + "' should contain a JSON array.");
                // You might need more specific validation for list content based on scenario
            }
            else {
                // For primitive values, compare directly after converting to String
                String actualValue = actualValueNode.asText();
                assertEquals(String.valueOf(expectedValue), actualValue,
                        "Mismatch for key '" + key + "': expected = " + expectedValue + ", actual = " + actualValue);
            }
        }
    }


}