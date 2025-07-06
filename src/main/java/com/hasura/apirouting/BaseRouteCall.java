package com.hasura.apirouting;

import com.hasura.modules.GraphQLUsersModule;
import org.jetbrains.annotations.NotNull;
import static com.hasura.config.global.APIsGlobalConfigs.GRAPHQL_USERS_MODULE; // Import the new module constant

public class BaseRouteCall {
    public static void routeGraphQLCallWithModuleAndScenarioName(@NotNull String moduleName, @NotNull String scenarioName) {
        switch (moduleName) {
            case GRAPHQL_USERS_MODULE: // Routing for users/todos operations
                GraphQLUsersModule.sendGraphQLRequestWithScenarioName(scenarioName);
                break;
            default:
                System.out.println("Unknown GraphQL Module Name: " + moduleName + " and Scenario Name: " + scenarioName);
        }
    }
}
