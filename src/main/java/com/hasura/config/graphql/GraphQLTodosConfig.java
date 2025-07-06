package com.hasura.config.graphql;

/**
 * Configuration class for GraphQL Todo-specific queries and scenarios.
 * This class should only contain constants related to todo operations.
 */
public class GraphQLTodosConfig {
    //############### Scenario Name ##############################################
    public static final String FETCH_ALL_TODOS_SCENARIO = "Fetch_All_Todos";
    public static final String FETCH_TODO_BY_ID_SCENARIO = "Fetch_Todo_By_Id";

    //########################### GraphQL Queries/Mutations #####################################

    // Query: Fetch all todos with associated user info
    public static final String FETCH_ALL_TODOS_QUERY = "query { todos { id title is_completed is_public user { id name } } }";

    // Query: Fetch a single todo by ID
    public static final String FETCH_TODO_BY_ID_QUERY = "query($todo_id: Int!) { todos(where: {id: {_eq: $todo_id}}) { id title is_completed is_public user { id name } } }";
}