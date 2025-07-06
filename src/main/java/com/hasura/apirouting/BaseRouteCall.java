package com.hasura.apirouting;

import com.hasura.modules.GraphQLTodosModule;
import com.hasura.modules.GraphQLUsersModule;
import org.jetbrains.annotations.NotNull;
import static com.hasura.config.global.APIsGlobalConfigs.GRAPHQL_TODOS_MODULE;
import static com.hasura.config.global.APIsGlobalConfigs.GRAPHQL_USERS_MODULE;

public class BaseRouteCall {
    public static void routeGraphQLCallWithModuleAndScenarioName(@NotNull String moduleName, @NotNull String scenarioName) {
        switch (moduleName) {
            case GRAPHQL_USERS_MODULE:
                GraphQLUsersModule.sendGraphQLRequestWithScenarioName(scenarioName);
                break;
            case GRAPHQL_TODOS_MODULE:
                GraphQLTodosModule.sendGraphQLRequestWithScenarioName(scenarioName);
                break;
            default:
                System.out.println("Unknown GraphQL Module Name: " + moduleName + " and Scenario Name: " + scenarioName);
        }
    }
}