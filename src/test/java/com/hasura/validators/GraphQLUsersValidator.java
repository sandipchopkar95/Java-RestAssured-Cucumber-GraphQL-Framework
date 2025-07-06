package com.hasura.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasura.config.graphql.GraphQLUsersConfig;
import com.hasura.modules.GraphQLUsersModule;
import java.util.Map;

import static com.hasura.stepdefinitions.CommonGraphQLSteps.validateJsonResponse; // Import the common helper
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contains specific validation logic for GraphQL Users module responses.
 */
public class GraphQLUsersValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void validateScenarioResponse(String scenarioName) {
        try {
            JsonNode actualDataNode;

            switch (scenarioName) {
                case GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO:
                case GraphQLUsersConfig.FETCH_USERS_ONLY_SCENARIO:
                    assertNotNull(GraphQLUsersModule.actualUsersResponse, "Actual users response is null.");
                    assertNotNull(GraphQLUsersModule.actualUsersResponse.getData(), "Users response 'data' field is null.");
                    actualDataNode = objectMapper.valueToTree(GraphQLUsersModule.actualUsersResponse.getData());

                    assertTrue(actualDataNode.has("users"), "'users' array not found in response data.");
                    assertTrue(actualDataNode.get("users").isArray(), "'users' field is not an array.");

                    if (scenarioName.equals(GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO)) {
                        assertTrue(actualDataNode.get("users").size() <= GraphQLUsersModule.dynamicLimit,
                                "Number of users returned (" + actualDataNode.get("users").size() +
                                        ") exceeds the specified limit (" + GraphQLUsersModule.dynamicLimit + ").");
                    }
                    if (actualDataNode.get("users").size() > 0) {
                        JsonNode firstUser = actualDataNode.get("users").get(0);
                        assertTrue(firstUser.has("id"), "User object missing 'id'.");
                        assertTrue(firstUser.has("name"), "User object missing 'name'.");
                        assertTrue(firstUser.has("todos"), "User object missing 'todos'.");
                        assertTrue(firstUser.get("todos").isArray(), "User todos is not an array.");
                    }
                    break;

                case GraphQLUsersConfig.CREATE_TODO_SCENARIO:
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
                    validateJsonResponse(returnedTodo, expectedDataMap); // Calling the common helper
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported Users module scenario for data validation: " + scenarioName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error validating GraphQL Users response data for scenario ["
                    + scenarioName + "]: " + e.getMessage(), e);
        }
    }
}