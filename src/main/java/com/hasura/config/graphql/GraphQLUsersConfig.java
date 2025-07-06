package com.hasura.config.graphql;

public class GraphQLUsersConfig {
    //############### Scenario Name ##############################################
    public static final String FETCH_USERS_WITH_TODOS_SCENARIO = "Fetch_Users_With_Todos";
    public static final String CREATE_TODO_SCENARIO = "Create_Todo";
    public static final String FETCH_USERS_ONLY_SCENARIO = "Fetch_Users_Only"; // For your second query example

    //########################### GraphQL Queries/Mutations #####################################

    // Query 1: Fetch users with their todos, with a limit variable
    public static final String FETCH_USERS_WITH_TODOS_QUERY = "query($limit : Int!){ users(limit:$limit){ id name todos(order_by:{created_at:desc}, limit:$limit){ id title } } }";

    // Query 2: Fetch users with just names and titles (simplified)
    public static final String FETCH_USERS_ONLY_QUERY = "query { users { name id todos { title } } }";

    // Mutation: Insert a new todo
    public static final String INSERT_TODO_MUTATION = "mutation ($todo_data:todos_insert_input!){ insert_todos(objects: [$todo_data]){ returning{ id title is_public is_completed } } }";
}
