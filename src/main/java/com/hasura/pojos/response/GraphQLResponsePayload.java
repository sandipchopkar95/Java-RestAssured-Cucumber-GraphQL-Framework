package com.hasura.pojos.response;

// --- IMPORTANT: Ensure these are from com.fasterxml.jackson.annotation ---
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
// -------------------------------------------------------------------------

import lombok.Data; // Assuming you have Lombok setup
import java.util.List;
import java.util.Map;

// Generic GraphQL response wrapper
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphQLResponsePayload<T> {
    private T data;
    private List<GraphQLError> errors;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GraphQLError {
        private String message;
        private List<Map<String, Integer>> locations;
        private List<String> path;
        private Map<String, Object> extensions;
    }

    // --- Specific POJOs for the 'data' field ---

    // For fetching users (query)
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserTodo {
        private int id;
        private String title;
        @JsonProperty("is_completed") // Add @JsonProperty for underscore-to-camelCase mapping
        private Boolean isCompleted; // Use Boolean for nullable fields
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String id;
        private String name;
        private List<UserTodo> todos;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsersQueryData { // Represents { "users": [...] }
        private List<User> users;
    }

    // --- Specific POJOs for the 'data' field of the Todo Mutation ---

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InsertTodoReturning { // Represents an individual object inside 'returning' array
        private int id;
        private String title;
        @JsonProperty("is_public") // Maps "is_public" from JSON to isPublic in Java
        private Boolean isPublic; // Use Boolean for nullable boolean or if server can send null
        @JsonProperty("is_completed") // Maps "is_completed" from JSON to isCompleted in Java
        private Boolean isCompleted; // Use Boolean for nullable boolean
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class InsertTodos { // This represents the 'insert_todos' object: { "returning": [...] }
        private List<InsertTodoReturning> returning;
    }

    // This class represents the ENTIRE 'data' object for the todo mutation: { "insert_todos": {...} }
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TodoMutationData { // Renamed from InsertTodosMutationData for clarity of purpose
        @JsonProperty("insert_todos") // Maps "insert_todos" from JSON to insertTodos in Java
        private InsertTodos insertTodos;
    }
}