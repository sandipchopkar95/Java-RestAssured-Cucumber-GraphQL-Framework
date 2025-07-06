package com.hasura.pojos.variables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * POJO representing the input object for inserting a new todo (`todos_insert_input`).
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodosInsertInput {
    private String title;
    @JsonProperty("is_public")
    private Boolean is_public;
}