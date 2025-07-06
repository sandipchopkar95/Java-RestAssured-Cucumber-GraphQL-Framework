package com.hasura.restAssuredAPICall;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.jetbrains.annotations.NotNull; // Assuming you still want to use JetBrains annotations

import static com.hasura.config.global.APIsGlobalConfigs.*;


/**
 * This class provides methods for making HTTP API calls using RestAssured,
 * specifically tailored for GraphQL requests.
 */
public class RestAssuredApiCall {

    /**
     * Sets the base URI for RestAssured requests.
     * While `postGraphQLRequest` sets it internally, this can be used for explicit setup if needed.
     * @param baseURL The base URL to set.
     */
    public static void setup(String baseURL) {
        RestAssured.baseURI = baseURL;
    }

    /**
     * Sends a GraphQL POST request.
     * Constructs the GraphQL specific JSON body {"query": "...", "variables": {...}}.
     *
     * @param endpoint The specific GraphQL endpoint relative to the base URL (e.g., "/graphql" or empty string if base URL includes it).
     * For Hasura.io/learn/graphql, this will likely be an empty string if GRAPHQL_BASE_URL is set to the full URL.
     * @param graphqlQuery The GraphQL query or mutation string (e.g., "query { users { id name } }").
     * @param variablesJson A JSON string of variables (e.g., "{\"limit\":5}"), or null if no variables are needed.
     * @return The RestAssured Response object.
     */
    public static @NotNull Response postGraphQLRequest(String endpoint, String graphqlQuery, String variablesJson) {
        String requestBody;
        // Construct the GraphQL request body JSON string: {"query":"<graphql_query>","variables":<variables_json>}
        if (variablesJson != null && !variablesJson.isEmpty()) {
            requestBody = String.format("{\"query\":\"%s\",\"variables\":%s}",
                    graphqlQuery.replace("\"", "\\\""), // Escape double quotes within the query string
                    variablesJson);
        } else {
            // If no variables, just include the query
            requestBody = String.format("{\"query\":\"%s\"}",
                    graphqlQuery.replace("\"", "\\\"")); // Escape double quotes within the query string
        }

        // Set the base URI specifically for GraphQL calls using the global config
        RestAssured.baseURI = GRAPHQL_BASE_URL;

        System.out.println("---- Sending GraphQL Request ----");
        System.out.println("Endpoint: " + RestAssured.baseURI + endpoint);
        System.out.println("Request Body:\n" + requestBody);

        Response response = RestAssured.given()
                .header("Content-type", "application/json")// GraphQL requests are always JSON POSTs
                .and()
                .header("Authorization","Bearer "+ USER_TOKEN)
                .and()
                .body(requestBody)
                .when()
                .post(endpoint) // Perform the POST request to the specified endpoint
                .then()
                .extract().response(); // Extract the response

        RESPONSE_MESSAGE = response.getBody().asString(); // Store the raw response body globally
        System.out.println("HTTP Response Status Code: " + response.statusCode());
        System.out.println("HTTP Response Body: \n" + RESPONSE_MESSAGE);
        System.out.println("---- GraphQL Request Complete ----\n");

        return response;
    }

    // All other REST-specific methods (like getRequest, putRequest, deleteRequest,
    // postRequestWithBody, etc.) have been removed to keep this class focused on GraphQL.
}