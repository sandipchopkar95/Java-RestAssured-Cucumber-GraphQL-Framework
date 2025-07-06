package com.hasura.pojos.variables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * POJO for the variables associated with the `insert_todos` GraphQL mutation.
 * This wraps the `todo_data` input object.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsertTodoMutationVariables {
    private TodosInsertInput todo_data;
}