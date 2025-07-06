package com.hasura.config.global;

public class APIsGlobalConfigs {
    // Updated GraphQL base URL for Hasura Learn
    public static final String GRAPHQL_BASE_URL = "https://hasura.io/learn/graphql";
    public static int HTTP_STATUS_CODE = -200;
    public static int RESPONSE_BODY_STATUS_CODE = -1; // Keep if GraphQL APIs return this (less common)
    public static String RESPONSE_MESSAGE = ""; // Keep for raw response body
    public static String RESPONSE_TODO_ID = ""; // To store the created TODO ID for chaining or validation
    public static String USER_TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ik9FWTJSVGM1UlVOR05qSXhSRUV5TURJNFFUWXdNekZETWtReU1EQXdSVUV4UVVRM05EazFNQSJ9.eyJodHRwczovL2hhc3VyYS5pby9qd3QvY2xhaW1zIjp7IngtaGFzdXJhLWRlZmF1bHQtcm9sZSI6InVzZXIiLCJ4LWhhc3VyYS1hbGxvd2VkLXJvbGVzIjpbInVzZXIiXSwieC1oYXN1cmEtdXNlci1pZCI6ImF1dGgwfDY4NGRjMzlmMWVhNWU2YjcxNTFkMTU3ZiJ9LCJuaWNrbmFtZSI6InNhbmRpcGttaWYiLCJuYW1lIjoic2FuZGlwa21pZkBnbWFpbC5jb20iLCJwaWN0dXJlIjoiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvNTA4YjlkYzMzOThkY2Q5ZGRlOTI3MTc2YmFkMTc0MTA_cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZzYS5wbmciLCJ1cGRhdGVkX2F0IjoiMjAyNS0wNy0wNVQxODoxMjoyNC4zMjBaIiwiaXNzIjoiaHR0cHM6Ly9ncmFwaHFsLXR1dG9yaWFscy5hdXRoMC5jb20vIiwiYXVkIjoiUDM4cW5GbzFsRkFRSnJ6a3VuLS13RXpxbGpWTkdjV1ciLCJzdWIiOiJhdXRoMHw2ODRkYzM5ZjFlYTVlNmI3MTUxZDE1N2YiLCJpYXQiOjE3NTE4MjQxMTgsImV4cCI6MTc1MTg2MDExOCwic2lkIjoiX21WZi16QUZhTENMcW1YRjFTdGhrUEU4NERtZ0Q1OUwiLCJhdF9oYXNoIjoiYjNDcnpBbzZxQnFBUW83Zm5SalpqdyIsIm5vbmNlIjoiOGpjSVRRTDVLMHZYM1gxWjBuYW1NRGN1WDRHWmkuZlkifQ.miLRCkzlleXdQe0Fbl8SYV4l_q7AcYXJisfaqRvlDCjU47Cq_363gVliEOe8w7N_XHAOaHA9PEjE7-1mP-OA5iHDpgCZiYtvRXqJuaJRcyM4HZEynh7tUdz00NXmtpviGwd4G7T8vDjjIuIeo-INZq4SampVLp0hgN9KCJYxoYhcS3WjRzqmLnvAckVOBbG-pcg-H95trB8JKqwXDzmC16Aw1IMFAPLLWNL9fQtXskc-Wpldnj6PueE3lQpBiqCOKG12ETKLoUY_95irMKb1a1To21Up_F-tXKAHitexGZ4eGKOPwVcH8CNdKLYryXbh71QBKOLxUcjmQfOOsE79xg";
    //############### Module Names ############################################################
    public static final String GRAPHQL_USERS_MODULE = "graphqlUsers"; // New module for Users & Todos
}
