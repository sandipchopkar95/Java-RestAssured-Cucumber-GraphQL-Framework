package com.hasura.pojos.variables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * POJO for the variables used in the GraphQL query to fetch a todo by its ID.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryTodoByIdVariables {
    private Integer todo_id;
}
