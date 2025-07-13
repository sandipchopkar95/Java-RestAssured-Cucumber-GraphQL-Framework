# Java-RestAssured-Cucumber-GraphQL-Framework

This project is a **BDD automation framework** built using **Java**, **Rest Assured**, **Cucumber**, and **GraphQL**. It is designed to test GraphQL APIs (e.g., Hasura) by defining feature files, reusable step definitions, and validation logic in a modular and maintainable structure.

---

## 🧱 Tech Stack

- **Java 11+**
- **Cucumber (BDD)**
- **Rest Assured**
- **GraphQL**
- **Maven**
- **JUnit**
- **POJOs & JSON Schema Mapping**
- **Gherkin Feature Files**

---

## 📁 Project Structure

```
src/
├── main/java/com/hasura/
│   ├── apirouting/                     # Base route for GraphQL API calls
│   ├── config/
│   │   ├── global/                     # Global configuration
│   │   ├── graphql/                    # GraphQL-specific configs
│   │   └── modules/                    # Module setup for GraphQL services
│   ├── pojos/response/                 # Response payload mappings
│   ├── variables/                      # Request variables for GraphQL queries/mutations
│   ├── restAssuredAPICall/            # Core RestAssured logic for making requests
│   └── utils/jsonToPojo/              # Utility for JSON to POJO conversion

test/
├── java/com/hasura/
│   ├── parameterConfig/               # Parameter types for Cucumber
│   ├── runners/                       # Cucumber test runners
│   ├── stepdefinitions/               # Step definitions for todos and users
│   └── validators/                    # Validators for response assertions

resources/
└── features/
    ├── graphql_todos.feature          # Feature file for Todos module
    └── graphql_users.feature          # Feature file for Users module

pom.xml                                # Maven dependencies
```

---

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- IDE (IntelliJ / Eclipse)
- Internet access for dependency resolution

---

### 🔧 Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/sandipchopkar95/Java-RestAssured-Cucumber-GraphQL-Framework.git
   cd Java-RestAssured-Cucumber-GraphQL-Framework
   ```

2. **Install dependencies**

   ```bash
   mvn clean install
   ```

3. **Run the tests**

   ```bash
   mvn test
   ```

---

## ✅ Features

- Modular folder structure separating config, variables, step definitions, and validators.
- Reusable step definitions for multiple GraphQL modules.
- End-to-end test automation using BDD.
- Clean JSON to POJO mapping using utility class.
- GraphQL query and mutation support with variable parameterization.

---

## 🧪 Example Feature Snippets

### `graphql_todos.feature`

```gherkin
Feature: Todos Module Testing

Scenario: Fetch Todo by ID
  Given user prepares GraphQL query to get Todo by ID
  When user executes the API call
  Then verify the response status code is 200
  And verify the returned Todo details
```

---

## 📂 Key Classes

- **BaseRouteCall.java** – Base endpoint routing logic.
- **RestAssuredApiCall.java** – Centralized RestAssured call handler.
- **GraphQL\*Config.java** – Defines queries/mutations for each module.
- **GraphQL\*Module.java** – Module registration and execution logic.
- **TodoSteps.java / UserSteps.java** – Step definitions for respective modules.
- **GraphQL\*Validator.java** – Validates API response.

---

## 🛠️ Customization

To add a new GraphQL module:

1. Add POJOs in `pojos/response` and `variables`.
2. Add config in `config/graphql` and `modules`.
3. Define steps and validators in `stepdefinitions` and `validators`.
4. Create a `.feature` file under `resources/features`.

---

## 🙌 Acknowledgements

Created and maintained by **Sandip Chopkar**.\
Feel free to fork, contribute, or raise issues.

