@GraphQL @Users
Feature: GraphQL Users and User-Initiated Todo Operations

  Scenario Outline: Fetch <scenario_desc> with dynamic limit
    Given I set the limit to <limit_value>
    When User calls GraphQL Users module with scenario "Fetch_Users_With_Todos"
    Then http_status_code is 200
    And GraphQL response has no errors
    And GraphQL response contains data for "Fetch_Users_With_Todos"
    Examples:
      | scenario_desc | limit_value |
      | 5 users       | 5           |
      | 2 users       | 2           |

  Scenario: Fetch Users with only name and ID
    When User calls GraphQL Users module with scenario "Fetch_Users_Only"
    Then http_status_code is 200
    And GraphQL response has no errors
    And GraphQL response contains data for "Fetch_Users_Only"

  Scenario Outline: Create a new Todo with <public_status> status and title "<title_value>"
    Given I set the todo title to "<title_value>" and public status to "<is_public>"
    When User calls GraphQL Users module with scenario "Create_Todo"
    Then http_status_code is 200
    And GraphQL response has no errors
    And GraphQL response contains data for "Create_Todo"
    Examples:
      | public_status | title_value         | is_public |
      | public        | My New Public Todo  | true      |
      | private       | My Private Task     | false     |