package com.hasura.modules;

import com.fasterxml.jackson.core.JsonProcessingException; // Import for ObjectMapper exception
import com.fasterxml.jackson.core.type.TypeReference; // Import for TypeReference
import com.fasterxml.jackson.databind.ObjectMapper; // Import for ObjectMapper
import com.github.javafaker.Faker;
import com.hasura.config.global.APIsGlobalConfigs;
import com.hasura.config.graphql.GraphQLUsersConfig;
import com.hasura.pojos.response.GraphQLResponsePayload;
import com.hasura.pojos.variables.InsertTodoMutationVariables;
import com.hasura.pojos.variables.QueryUsersVariables;
import com.hasura.pojos.variables.TodosInsertInput;
import com.hasura.restAssuredAPICall.RestAssuredApiCall;
import com.hasura.utils.jsonToPojo.JsonUtils;
import io.restassured.response.Response;
import org.jetbrains.annotations.NotNull;

public class GraphQLUsersModule {

    private static final Faker faker = new Faker();
    private static final ObjectMapper objectMapper = new ObjectMapper(); // Initialize ObjectMapper once

    // CORRECTED: Generic type for actualTodoMutationResponse to reflect the expected data structure
    public static GraphQLResponsePayload<GraphQLResponsePayload.UsersQueryData> actualUsersResponse;
    public static GraphQLResponsePayload<GraphQLResponsePayload.TodoMutationData> actualTodoMutationResponse; // Changed to TodoMutationData

    // Dynamic data holders
    public static int dynamicLimit;
    public static String dynamicTodoTitle;
    public static Boolean dynamicTodoIsPublic;

    // This POJO will hold the expected data for the 'returning' object from the mutation response
    public static GraphQLResponsePayload.InsertTodoReturning expectedTodoReturningData;

    /**
     * Sends a GraphQL request based on the provided scenario name.
     * Data like 'limit', 'title', 'is_public' are now dynamic.
     * @param scenarioName The name of the GraphQL scenario to execute.
     */
    public static void sendGraphQLRequestWithScenarioName(@NotNull String scenarioName) {
        String graphqlQuery;
        String variablesJson = null;

        // Reset response objects at the start of each request
        actualUsersResponse = null;
        actualTodoMutationResponse = null;
        expectedTodoReturningData = null; // Reset this too, it will be recreated for Create_Todo

        switch (scenarioName) {
            case GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO:
                graphqlQuery = GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_QUERY;
                QueryUsersVariables usersVariables = new QueryUsersVariables();
                usersVariables.setLimit(dynamicLimit); // Use the dynamic limit
                variablesJson = JsonUtils.objectToJsonString(usersVariables);
                break;

            case GraphQLUsersConfig.FETCH_USERS_ONLY_SCENARIO:
                graphqlQuery = GraphQLUsersConfig.FETCH_USERS_ONLY_QUERY;
                // No variables for this specific query
                break;

            case GraphQLUsersConfig.CREATE_TODO_SCENARIO:
                graphqlQuery = GraphQLUsersConfig.INSERT_TODO_MUTATION;
                TodosInsertInput todoInput = new TodosInsertInput();
                todoInput.setTitle(dynamicTodoTitle); // Use the dynamic title
                todoInput.setIs_public(dynamicTodoIsPublic); // Use the dynamic is_public

                InsertTodoMutationVariables mutationVariables = new InsertTodoMutationVariables();
                mutationVariables.setTodo_data(todoInput);
                variablesJson = JsonUtils.objectToJsonString(mutationVariables);

                // Initialize and populate expectedTodoReturningData based on input
                expectedTodoReturningData = new GraphQLResponsePayload.InsertTodoReturning();
                expectedTodoReturningData.setTitle(dynamicTodoTitle);
                expectedTodoReturningData.setIsPublic(dynamicTodoIsPublic);
                expectedTodoReturningData.setIsCompleted(false);

                break;

            default:
                throw new IllegalArgumentException("Unknown GraphQL scenario name: " + scenarioName);
        }

        Response response = RestAssuredApiCall.postGraphQLRequest("", graphqlQuery, variablesJson);
        APIsGlobalConfigs.HTTP_STATUS_CODE = response.getStatusCode();
        String responseBody = response.getBody().asString(); // Get response body as string for ObjectMapper

        // Deserialize the full GraphQL response based on the scenario
        try {
            if (scenarioName.equals(GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO) ||
                    scenarioName.equals(GraphQLUsersConfig.FETCH_USERS_ONLY_SCENARIO)) {
                // Use TypeReference for correct generic deserialization
                actualUsersResponse = objectMapper.readValue(responseBody,
                        new TypeReference<GraphQLResponsePayload<GraphQLResponsePayload.UsersQueryData>>() {});

            } else if (scenarioName.equals(GraphQLUsersConfig.CREATE_TODO_SCENARIO)) {
                // Correctly deserialize to the type that matches the 'data' field of the mutation response
                actualTodoMutationResponse = objectMapper.readValue(responseBody,
                        new TypeReference<GraphQLResponsePayload<GraphQLResponsePayload.TodoMutationData>>() {});

                // Extract created TODO ID for chaining and update expected data
                if (actualTodoMutationResponse != null && actualTodoMutationResponse.getData() != null) {
                    // Access the insertTodos object via the TodoMutationData
                    GraphQLResponsePayload.InsertTodos mutationData = actualTodoMutationResponse.getData().getInsertTodos();
                    if (mutationData != null && mutationData.getReturning() != null && !mutationData.getReturning().isEmpty()) {
                        GraphQLResponsePayload.InsertTodoReturning createdTodo = mutationData.getReturning().get(0);
                        APIsGlobalConfigs.RESPONSE_TODO_ID = String.valueOf(createdTodo.getId());
                        System.out.println("GraphQL Created TODO ID: " + APIsGlobalConfigs.RESPONSE_TODO_ID);
                        // Update expected ID, as it's generated by the server
                        expectedTodoReturningData.setId(createdTodo.getId());
                    }
                }
            }
        } catch (JsonProcessingException e) {
            System.err.println("Failed to deserialize GraphQL response for scenario " + scenarioName + ": " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            // Ensure response objects are null on failure
            actualUsersResponse = null;
            actualTodoMutationResponse = null;
        } catch (Exception e) { // Catch any other potential exceptions during processing
            System.err.println("An unexpected error occurred during GraphQL response processing for scenario " + scenarioName + ": " + e.getMessage());
            e.printStackTrace();
            actualUsersResponse = null;
            actualTodoMutationResponse = null;
        }
    }
}