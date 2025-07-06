package com.hasura.stepdefinitions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasura.config.global.APIsGlobalConfigs;
import com.hasura.config.graphql.GraphQLTodosConfig; // Still needed for constants
import com.hasura.config.graphql.GraphQLUsersConfig; // Still needed for constants
import com.hasura.modules.GraphQLTodosModule;
import com.hasura.modules.GraphQLUsersModule;
import com.hasura.validators.GraphQLTodosValidator; // Import new validator
import com.hasura.validators.GraphQLUsersValidator; // Import new validator
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.hasura.config.global.APIsGlobalConfigs.GRAPHQL_TODOS_MODULE;
import static com.hasura.config.global.APIsGlobalConfigs.GRAPHQL_USERS_MODULE;

/**
 * Contains common GraphQL step definitions applicable across different modules.
 * This class now acts as a dispatcher for module-specific data validations.
 */
public class CommonGraphQLSteps {

    // ObjectMapper is still needed here for the generic validateJsonResponse method
    private final ObjectMapper objectMapper = new ObjectMapper();

    // These fields will be set by the calling step definition (e.g., UserSteps or TodoSteps)
    // to provide context for common validations.
    public static String currentFeatureModuleName;
    public static String currentFeatureScenarioName;

    @Then("http_status_code is {int}")
    public void http_status_codeIsHttpCode(int httpCode) {
        assertEquals(httpCode, APIsGlobalConfigs.HTTP_STATUS_CODE, "HTTP Status Code mismatch.");
    }

    @And("GraphQL response has no errors")
    public void graphql_response_has_no_errors() {
        // Dynamically check the correct module's response object for errors
        switch (currentFeatureModuleName) {
            case GRAPHQL_USERS_MODULE:
                switch (currentFeatureScenarioName) {
                    case GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO:
                    case GraphQLUsersConfig.FETCH_USERS_ONLY_SCENARIO:
                        assertNotNull(GraphQLUsersModule.actualUsersResponse, "GraphQL Users response object is null.");
                        assertTrue(GraphQLUsersModule.actualUsersResponse.getErrors() == null ||
                                        GraphQLUsersModule.actualUsersResponse.getErrors().isEmpty(),
                                "GraphQL Users response contains errors: " + (GraphQLUsersModule.actualUsersResponse.getErrors() != null ?
                                        GraphQLUsersModule.actualUsersResponse.getErrors().toString() : ""));
                        break;
                    case GraphQLUsersConfig.CREATE_TODO_SCENARIO:
                        assertNotNull(GraphQLUsersModule.actualTodoMutationResponse, "GraphQL Todo Mutation response object is null.");
                        assertTrue(GraphQLUsersModule.actualTodoMutationResponse.getErrors() == null ||
                                        GraphQLUsersModule.actualTodoMutationResponse.getErrors().isEmpty(),
                                "GraphQL Todo Mutation response contains errors: " + (GraphQLUsersModule.actualTodoMutationResponse.getErrors() != null ?
                                        GraphQLUsersModule.actualTodoMutationResponse.getErrors().toString() : ""));
                        break;
                    default:
                        throw new IllegalStateException("Unhandled Users module scenario for error validation: " + currentFeatureScenarioName);
                }
                break;

            case GRAPHQL_TODOS_MODULE:
                assertNotNull(GraphQLTodosModule.actualTodosResponse, "GraphQL Todos response object is null.");
                assertTrue(GraphQLTodosModule.actualTodosResponse.getErrors() == null ||
                                GraphQLTodosModule.actualTodosResponse.getErrors().isEmpty(),
                        "GraphQL Todos response contains errors: " + (GraphQLTodosModule.actualTodosResponse.getErrors() != null ?
                                GraphQLTodosModule.actualTodosResponse.getErrors().toString() : ""));
                break;

            default:
                throw new IllegalStateException("Unhandled GraphQL module for error validation: " + currentFeatureModuleName);
        }
        System.out.println("GraphQL response has no errors.");
    }

    @And("GraphQL response contains data for {string}")
    public void graphql_response_contains_data_for(String scenarioName) {
        try {
            switch (currentFeatureModuleName) {
                case GRAPHQL_USERS_MODULE:
                    GraphQLUsersValidator.validateScenarioResponse(scenarioName); // Delegate to Users validator
                    break;

                case GRAPHQL_TODOS_MODULE:
                    GraphQLTodosValidator.validateScenarioResponse(scenarioName); // Delegate to Todos validator
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported GraphQL module for data validation: " + currentFeatureModuleName);
            }
            System.out.println("GraphQL response 'data' matches expected for scenario: " + scenarioName);
        } catch (Exception e) {
            throw new RuntimeException("Error validating GraphQL response 'data' for scenario ["
                    + scenarioName + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to recursively validate a JSON Node against a map of expected values.
     * This is generic and can be used across modules for validating object structures.
     * This method remains here because it is a general utility, not specific to any single module's data.
     * @param jsonResponse The actual JSON Node from the response.
     * @param expectedValues A map of key-value pairs representing the expected data.
     */
    public static void validateJsonResponse(JsonNode jsonResponse, Map<String, Object> expectedValues) {
        for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
            String key = entry.getKey();
            Object expectedValue = entry.getValue();

            if (key.equals("id")) {
                assertTrue(jsonResponse.has(key), "Key '" + key + "' is missing in the response.");
                assertTrue(jsonResponse.get(key).asInt() > 0, "ID for '" + key + "' must be a positive integer.");
                continue;
            }

            assertTrue(jsonResponse.has(key), "Key '" + key + "' is missing in the response.");
            JsonNode actualValueNode = jsonResponse.get(key);

            if (expectedValue instanceof Map) {
                assertTrue(actualValueNode.isObject(), "Key '" + key + "' should contain a JSON object.");
                validateJsonResponse(actualValueNode, (Map<String, Object>) expectedValue);
            } else if (expectedValue instanceof List) {
                assertTrue(actualValueNode.isArray(), "Key '" + key + "' should contain a JSON array.");
            }
            else {
                String actualValue = actualValueNode.asText();
                assertEquals(String.valueOf(expectedValue), actualValue,
                        "Mismatch for key '" + key + "': expected = " + expectedValue + ", actual = " + actualValue);
            }
        }
    }
}