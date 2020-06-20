package de.tzander.keycloak.configurator;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.AuthenticationManagementResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeycloakKonfigurator {

    public static final Logger LOGGER = LoggerFactory.getLogger(KeycloakKonfigurator.class);

    /**
     * arg[0] serverUrl - https://url.de/auth  The Keycloak URL
     * arg[1] redirectUrl - http://localhost:8080/auth/realms/realmy/account
     * arg[2] adminPassword
     *
     * @param args
     */
    public static void main(String[] args) {

        String serverUrl = args[0];
        String redirectUrl = args[1];
        String adminPassword = args[2];
        String realm = "master";
        String newRealm = "realmy";
        String displayName = "REALM COMPANY";
        String newClient = "yourClient";
        String protocol = "openid-connect";
        String theme = "yourCustomTheme";
        String passwordPolicy = "notUsername(undefined) and digits(1) and upperCase(1) and length(8) and lowerCase(1) " +
                "and passwordHistory(5) and specialChars(1)";
        String maxCountWrongAnswer = "5";

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username("admin")
                .password(adminPassword)
                .build();

        RealmRepresentation realmRepresentation = RealmRepresentationFactory.create(newRealm, theme, displayName, passwordPolicy);

        RealmsResource realmsResource = keycloak.realms();
        realmsResource.realm(newRealm).remove();
        realmsResource.create(realmRepresentation);
        LOGGER.info("Realm " + realm + " created");

        ClientRepresentation clientRepresentation = ClientRepresentationFactory.create(newClient, redirectUrl, protocol);

        ProtocolMapperRepresentation protocolMapperRepresentation = ProtocolMapperRepresentationFactory.create(protocol);
        List<ProtocolMapperRepresentation> mappers = new ArrayList<>();
        mappers.add(protocolMapperRepresentation);

        ClientScopeRepresentation clientScopeRepresentation = ClientScopeRepresentationFactory.create(mappers);
        RealmResource realmResource = keycloak.realm(newRealm);
        Response clientScopeResponse = realmResource.clientScopes().create(clientScopeRepresentation);
        LOGGER.info("ClientScope {}", clientScopeResponse.getStatusInfo());

        clientRepresentation.setProtocolMappers(mappers);
        Response response = realmResource.clients().create(clientRepresentation);
        LOGGER.info("Client {} was {}", clientRepresentation.getName(), response.getStatusInfo());

        AuthenticationFlowRepresentation credentials = configureAuthManagement(newRealm, keycloak, maxCountWrongAnswer);

        AuthenticationFlowUpdateService updateRealmService = new AuthenticationFlowUpdateService(keycloak, realmsResource);
        updateRealmService.updateRealm(newRealm, credentials);
    }

    private static AuthenticationFlowRepresentation configureAuthManagement(String newRealm, Keycloak keycloak, String maxCountWrongAnswer) {
        AuthenticationManagementResource managementResource = keycloak.realm(newRealm).flows();
        List<RequiredActionProviderSimpleRepresentation> result = managementResource.getUnregisteredRequiredActions();
        LOGGER.info("Register flow: " + result.get(0).getName());
        managementResource.registerRequiredAction(result.get(0));

        AuthenticationFlowRepresentation credentials = new AuthenticationFlowRepresentation();
        credentials.setAlias("REALM COMPANY - Passwort vergessen");
        credentials.setDescription("Passwort vergessen Prozess inklusive Sicherheitsfrage");
        credentials.setProviderId("basic-flow");
        credentials.setBuiltIn(false);
        credentials.setTopLevel(true);
        Response resp = managementResource.createFlow(credentials);

        Map<String, String> chooseUserMap = new HashMap<>();
        chooseUserMap.put("alias", "Choose User");
        chooseUserMap.put("type", "basic-flow");
        chooseUserMap.put("provider", "reset-cred-choose-user-captcha");
        managementResource.addExecution(credentials.getAlias(), chooseUserMap);

        Map<String, String> questionMap = new HashMap<>();
        questionMap.put("alias", "Secret Question");
        questionMap.put("type", "basic-flow");
        questionMap.put("provider", "secret-question-authenticator");
        managementResource.addExecution(credentials.getAlias(), questionMap);

        Map<String, String> emailMap = new HashMap<>();
        emailMap.put("alias", "Send Reset Email");
        emailMap.put("type", "basic-flow");
        emailMap.put("provider", "reset-credential-email");
        managementResource.addExecution(credentials.getAlias(), emailMap);

        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put("alias", "Reset Password");
        passwordMap.put("type", "basic-flow");
        passwordMap.put("provider", "reset-password");
        managementResource.addExecution(credentials.getAlias(), passwordMap);

        LOGGER.info("Flow added: " + resp.getStatus());

        AuthenticatorConfigRepresentation config = createAuthenticatorConfigRepresentations(maxCountWrongAnswer);
        List<AuthenticationExecutionInfoRepresentation> executionList = managementResource.getExecutions(credentials.getAlias());
        Response configResponse = managementResource.newExecutionConfig(executionList.get(1).getId(), config);

        LOGGER.info("AuthenticatorConfigRepresentation added: " + configResponse.getStatus());
        return credentials;
    }

    private static  AuthenticatorConfigRepresentation createAuthenticatorConfigRepresentations(String maxCountWrongAnswer) {
        AuthenticatorConfigRepresentation configRepresentation = new AuthenticatorConfigRepresentation();
        configRepresentation.setAlias("Max. Anzahl falscher Antworten");
        Map<String, String> map = new HashMap<>();
        map.put("login.failure.count", maxCountWrongAnswer);
        configRepresentation.setConfig(map);
        return configRepresentation;
    }
}
