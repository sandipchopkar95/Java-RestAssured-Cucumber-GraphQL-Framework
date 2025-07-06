package com.hasura.pojos.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Generic POJO to represent the top-level structure of a GraphQL response.
 * It contains a 'data' field (which can be of various types) and an optional 'errors' field.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphQLResponsePayload<T> {
    private T data;
    private List<GraphQLError> errors;

    /**
     * Represents a single error object within the 'errors' array of a GraphQL response.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GraphQLError {
        private String message;
        private List<Map<String, Integer>> locations;
        private List<String> path;
        private Map<String, Object> extensions;
    }

    // --- POJOs for the 'data' field of *Users-related* queries/mutations ---

    /**
     * Represents a single Todo object as part of a User's todos list.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserTodo {
        private int id;
        private String title;
        @JsonProperty("is_completed")
        private Boolean isCompleted;
        @JsonProperty("is_public")
        private Boolean isPublic;
    }

    /**
     * Represents a single User object from a GraphQL query.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String id;
        private String name;
        private List<UserTodo> todos;
    }

    /**
     * Represents the 'data' payload for GraphQL queries that return a list of 'users'.
     * E.g., `{"data": {"users": [...]}}`
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsersQueryData {
        private List<User> users;
    }

    /**
     * Represents the 'returning' array object within an insert mutation response.
     * E.g., `{"returning": [{"id": 1, "title": "...", "is_public": true, "is_completed": false}]}`
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InsertTodoReturning {
        private int id;
        private String title;
        @JsonProperty("is_public")
        private Boolean isPublic;
        @JsonProperty("is_completed")
        private Boolean isCompleted;
    }

    /**
     * Represents the `insert_todos` object within the data payload of a todo mutation.
     * E.g., `{"insert_todos": {"returning": [...]}}`
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InsertTodos {
        private List<InsertTodoReturning> returning;
    }

    /**
     * Represents the entire 'data' payload for a todo *mutation* response.
     * E.g., `{"data": {"insert_todos": {...}}}`
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TodoMutationData {
        @JsonProperty("insert_todos")
        private InsertTodos insertTodos;
    }


    // --- POJOs for the 'data' field of *Todos-related* queries ---

    /**
     * Represents a single Todo object from a GraphQL query when fetching todos directly.
     * This might include user details if the query joins them.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Todo {
        private int id;
        private String title;
        @JsonProperty("is_completed")
        private Boolean isCompleted;
        @JsonProperty("is_public")
        private Boolean isPublic;
        private User user; // Include user info for the todo
    }

    /**
     * Represents the 'data' payload for GraphQL queries that return a list of 'todos'.
     * E.g., `{"data": {"todos": [...]}}`
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TodosQueryData {
        private List<Todo> todos;
    }
}