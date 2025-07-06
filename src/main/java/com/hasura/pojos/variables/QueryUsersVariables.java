package com.hasura.pojos.variables;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// Reusing this for the query variables payload
// Represents the 'variables' part of the GraphQL request for the users query
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryUsersVariables {
    private Integer limit;
}



