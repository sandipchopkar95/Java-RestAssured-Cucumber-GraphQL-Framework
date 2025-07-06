package com.hasura.modules;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hasura.config.global.APIsGlobalConfigs;
import com.hasura.config.graphql.GraphQLTodosConfig;
import com.hasura.pojos.response.GraphQLResponsePayload;
import com.hasura.pojos.variables.QueryTodoByIdVariables;
import com.hasura.restAssuredAPICall.RestAssuredApiCall;
import com.hasura.utils.jsonToPojo.JsonUtils;
import io.restassured.response.Response;
import org.jetbrains.annotations.NotNull;

/**
 * Module responsible for handling GraphQL operations specifically related to Todos.
 * It manages its own response objects and dynamic data relevant only to Todo queries.
 */
public class GraphQLTodosModule {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Response object specific to Todos module operations
    public static GraphQLResponsePayload<GraphQLResponsePayload.TodosQueryData> actualTodosResponse;

    // Dynamic data specific to Todos module operations
    public static int dynamicTodoId;

    /**
     * Sends a GraphQL request for todo-specific operations based on the provided scenario name.
     *
     * @param scenarioName The name of the GraphQL scenario to execute.
     */
    public static void sendGraphQLRequestWithScenarioName(@NotNull String scenarioName) {
        String graphqlQuery;
        String variablesJson = null;

        actualTodosResponse = null; // Reset response object for this module

        switch (scenarioName) {
            case GraphQLTodosConfig.FETCH_ALL_TODOS_SCENARIO:
                graphqlQuery = GraphQLTodosConfig.FETCH_ALL_TODOS_QUERY;
                break;

            case GraphQLTodosConfig.FETCH_TODO_BY_ID_SCENARIO:
                graphqlQuery = GraphQLTodosConfig.FETCH_TODO_BY_ID_QUERY;
                QueryTodoByIdVariables todoByIdVariables = new QueryTodoByIdVariables();
                todoByIdVariables.setTodo_id(dynamicTodoId);
                variablesJson = JsonUtils.objectToJsonString(todoByIdVariables);
                break;

            default:
                throw new IllegalArgumentException("Unknown GraphQL Todos scenario name: " + scenarioName);
        }

        Response response = RestAssuredApiCall.postGraphQLRequest("", graphqlQuery, variablesJson);
        APIsGlobalConfigs.HTTP_STATUS_CODE = response.getStatusCode();
        String responseBody = response.getBody().asString();

        try {
            switch (scenarioName) {
                case GraphQLTodosConfig.FETCH_ALL_TODOS_SCENARIO:
                case GraphQLTodosConfig.FETCH_TODO_BY_ID_SCENARIO:
                    actualTodosResponse = objectMapper.readValue(responseBody,
                            new TypeReference<GraphQLResponsePayload<GraphQLResponsePayload.TodosQueryData>>() {});
                    break;

                default:
                    System.err.println("No specific deserialization logic for scenario: " + scenarioName + " in GraphQLTodosModule.");
                    break;
            }

        } catch (JsonProcessingException e) {
            System.err.println("Failed to deserialize GraphQL Todos response for scenario " + scenarioName + ": " + e.getMessage());
            e.printStackTrace();
            actualTodosResponse = null;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during GraphQL Todos response processing for scenario " + scenarioName + ": " + e.getMessage());
            e.printStackTrace();
            actualTodosResponse = null;
        }
    }
}