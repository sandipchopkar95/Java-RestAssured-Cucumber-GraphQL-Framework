package com.hasura.pojos.variables;


import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// This would be the wrapper for the todo_data variable in the mutation
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class InsertTodoMutationVariables {
        private TodosInsertInput todo_data;
    }
