package com.hasura.modules;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * Module responsible for handling GraphQL operations specifically related to Users
 * and the creation of Todos *through* the user context (as an authenticated user).
 * It manages its own response objects and dynamic data relevant only to User queries/mutations.
 */
public class GraphQLUsersModule {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Response objects specific to Users module operations
    public static GraphQLResponsePayload<GraphQLResponsePayload.UsersQueryData> actualUsersResponse;
    public static GraphQLResponsePayload<GraphQLResponsePayload.TodoMutationData> actualTodoMutationResponse;

    // Dynamic data specific to Users module operations
    public static int dynamicLimit;
    public static String dynamicTodoTitle;
    public static Boolean dynamicTodoIsPublic;

    // Expected data POJO for validating the Create_Todo mutation response
    public static GraphQLResponsePayload.InsertTodoReturning expectedTodoReturningData;

    /**
     * Sends a GraphQL request based on the provided scenario name, specific to user operations.
     * Dynamic data like 'limit', 'title', 'is_public' are handled here.
     *
     * @param scenarioName The name of the GraphQL scenario to execute.
     */
    public static void sendGraphQLRequestWithScenarioName(@NotNull String scenarioName) {
        String graphqlQuery;
        String variablesJson = null;

        // Reset response objects at the start of each request for this module
        actualUsersResponse = null;
        actualTodoMutationResponse = null;
        expectedTodoReturningData = null;

        switch (scenarioName) {
            case GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO:
                graphqlQuery = GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_QUERY;
                QueryUsersVariables usersVariables = new QueryUsersVariables();
                usersVariables.setLimit(dynamicLimit);
                variablesJson = JsonUtils.objectToJsonString(usersVariables);
                break;

            case GraphQLUsersConfig.FETCH_USERS_ONLY_SCENARIO:
                graphqlQuery = GraphQLUsersConfig.FETCH_USERS_ONLY_QUERY;
                break;

            case GraphQLUsersConfig.CREATE_TODO_SCENARIO:
                graphqlQuery = GraphQLUsersConfig.INSERT_TODO_MUTATION;
                TodosInsertInput todoInput = new TodosInsertInput();
                todoInput.setTitle(dynamicTodoTitle);
                todoInput.setIs_public(dynamicTodoIsPublic);

                InsertTodoMutationVariables mutationVariables = new InsertTodoMutationVariables();
                mutationVariables.setTodo_data(todoInput);
                variablesJson = JsonUtils.objectToJsonString(mutationVariables);

                expectedTodoReturningData = new GraphQLResponsePayload.InsertTodoReturning();
                expectedTodoReturningData.setTitle(dynamicTodoTitle);
                expectedTodoReturningData.setIsPublic(dynamicTodoIsPublic);
                expectedTodoReturningData.setIsCompleted(false);

                break;

            default:
                throw new IllegalArgumentException("Unknown GraphQL Users scenario name: " + scenarioName);
        }
        Response response = RestAssuredApiCall.postGraphQLRequest("", graphqlQuery, variablesJson);
        APIsGlobalConfigs.HTTP_STATUS_CODE = response.getStatusCode();
        String responseBody = response.getBody().asString();

        try {
            // Using a switch statement for deserialization based on scenarioName
            switch (scenarioName) {
                case GraphQLUsersConfig.FETCH_USERS_WITH_TODOS_SCENARIO:
                case GraphQLUsersConfig.FETCH_USERS_ONLY_SCENARIO:
                    actualUsersResponse = objectMapper.readValue(responseBody,
                            new TypeReference<GraphQLResponsePayload<GraphQLResponsePayload.UsersQueryData>>() {
                            });
                    break;

                case GraphQLUsersConfig.CREATE_TODO_SCENARIO:
                    actualTodoMutationResponse = objectMapper.readValue(responseBody,
                            new TypeReference<GraphQLResponsePayload<GraphQLResponsePayload.TodoMutationData>>() {
                            });

                    if (actualTodoMutationResponse != null && actualTodoMutationResponse.getData() != null) {
                        GraphQLResponsePayload.InsertTodos mutationData = actualTodoMutationResponse.getData().getInsertTodos();
                        if (mutationData != null && mutationData.getReturning() != null && !mutationData.getReturning().isEmpty()) {
                            GraphQLResponsePayload.InsertTodoReturning createdTodo = mutationData.getReturning().get(0);
                            APIsGlobalConfigs.RESPONSE_TODO_ID = String.valueOf(createdTodo.getId());
                            System.out.println("GraphQL Created TODO ID: " + APIsGlobalConfigs.RESPONSE_TODO_ID);
                            expectedTodoReturningData.setId(createdTodo.getId());
                        }
                    }
                    break;

                default:
                    System.err.println("No specific deserialization logic for scenario: " + scenarioName);
                    break;
            }
        } catch (JsonProcessingException e) {
            System.err.println("Failed to deserialize GraphQL Users response for scenario " + scenarioName + ": " + e.getMessage());
            e.printStackTrace();
            actualUsersResponse = null;
            actualTodoMutationResponse = null;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during GraphQL Users response processing for scenario " + scenarioName + ": " + e.getMessage());
            e.printStackTrace();
            actualUsersResponse = null;
            actualTodoMutationResponse = null;
        }
    }
}
