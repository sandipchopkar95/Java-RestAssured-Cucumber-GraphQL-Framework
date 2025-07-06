package com.hasura.parameterConfig; // Or com.hasura.stepdefinitions, depending on where you put it

import io.cucumber.java.ParameterType;

public class ParameterTypes {

    @ParameterType("true|false") // This regex matches the literal strings "true" or "false"
    public Boolean booleanValue(String value) {
        // This converts the matched string to a Java Boolean object
        return Boolean.parseBoolean(value);
    }
}