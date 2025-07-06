package com.hasura.pojos.variables;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// Input for the 'insert_todos' mutation
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public  class TodosInsertInput {
        private String title;
        private Boolean is_public;
        // Add other fields if needed, e.g., 'user_id' if you want to assign todos to specific users.
    }
