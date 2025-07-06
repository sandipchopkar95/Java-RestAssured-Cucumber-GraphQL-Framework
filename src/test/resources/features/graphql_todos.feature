@GraphQL @Todos
Feature: GraphQL Todo-Specific Operations

  Scenario: Fetch all Todos
    Given User calls GraphQL Todos module with scenario "Fetch_All_Todos"
    Then http_status_code is 200
    And GraphQL response has no errors
    And GraphQL response contains data for "Fetch_All_Todos"

  Scenario Outline: Fetch a Todo by ID <todo_id_desc>
    Given I set the todo ID to <todo_id_value>
    When User calls GraphQL Todos module with scenario "Fetch_Todo_By_Id"
    Then http_status_code is 200
    And GraphQL response has no errors
    And GraphQL response contains data for "Fetch_Todo_By_Id"
    Examples:
      | todo_id_desc | todo_id_value |
      | 571          | 571           |
      | 580          | 580           |