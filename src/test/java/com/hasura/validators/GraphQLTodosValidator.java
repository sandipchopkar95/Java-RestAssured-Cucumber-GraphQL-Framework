package com.hasura.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasura.config.graphql.GraphQLTodosConfig;
import com.hasura.modules.GraphQLTodosModule;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contains specific validation logic for GraphQL Todos module responses.
 */
public class GraphQLTodosValidator {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void validateScenarioResponse(String scenarioName) {
        try {
            JsonNode actualDataNode;

            switch (scenarioName) {
                case GraphQLTodosConfig.FETCH_ALL_TODOS_SCENARIO:
                case GraphQLTodosConfig.FETCH_TODO_BY_ID_SCENARIO:
                    assertNotNull(GraphQLTodosModule.actualTodosResponse, "Actual todos response is null.");
                    assertNotNull(GraphQLTodosModule.actualTodosResponse.getData(), "Todos response 'data' field is null.");
                    actualDataNode = objectMapper.valueToTree(GraphQLTodosModule.actualTodosResponse.getData());

                    assertTrue(actualDataNode.has("todos"), "'todos' array not found in response data.");
                    assertTrue(actualDataNode.get("todos").isArray(), "'todos' field is not an array.");

                    if (scenarioName.equals(GraphQLTodosConfig.FETCH_ALL_TODOS_SCENARIO)) {
                        assertTrue(actualDataNode.get("todos").size() > 0, "No todos returned for Fetch_All_Todos.");
                        if (actualDataNode.get("todos").size() > 0) {
                            JsonNode firstTodo = actualDataNode.get("todos").get(0);
                            assertTrue(firstTodo.has("id"), "Todo object missing 'id'.");
                            assertTrue(firstTodo.has("title"), "Todo object missing 'title'.");
                            assertTrue(firstTodo.has("user"), "Todo object missing 'user'.");
                            assertTrue(firstTodo.get("user").isObject(), "Todo 'user' is not an object.");
                        }
                    } else if (scenarioName.equals(GraphQLTodosConfig.FETCH_TODO_BY_ID_SCENARIO)) {
                        assertTrue(actualDataNode.get("todos").size() == 1, "Expected 1 todo for Fetch_Todo_By_Id, but got " + actualDataNode.get("todos").size());
                        JsonNode fetchedTodo = actualDataNode.get("todos").get(0);
                        assertEquals(GraphQLTodosModule.dynamicTodoId, fetchedTodo.get("id").asInt(), "Fetched todo ID mismatch.");
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported Todos module scenario for data validation: " + scenarioName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error validating GraphQL Todos response data for scenario ["
                    + scenarioName + "]: " + e.getMessage(), e);
        }
    }
}