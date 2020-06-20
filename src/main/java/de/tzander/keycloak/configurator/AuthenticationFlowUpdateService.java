package de.tzander.keycloak.configurator;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RealmsResource;
import org.keycloak.representations.idm.AuthenticationExecutionInfoRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class AuthenticationFlowUpdateService {

    public static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFlowUpdateService.class);

    private RealmsResource realmsResource;
    private Keycloak keycloak;

    public AuthenticationFlowUpdateService(Keycloak keycloak, RealmsResource realmsResource) {
        this.realmsResource = realmsResource;
        this.keycloak = keycloak;
    }

    public void updateRealm(String newRealm, AuthenticationFlowRepresentation authenticationFlowRepresentation) {
        List<AuthenticationExecutionInfoRepresentation> executionReps = keycloak.realm(newRealm).flows()
                .getExecutions(authenticationFlowRepresentation.getAlias());

        setExecutionToRequired(newRealm, keycloak, authenticationFlowRepresentation, executionReps, "secret-question-authenticator");
        setExecutionToRequired(newRealm, keycloak, authenticationFlowRepresentation, executionReps, "reset-credential-email");
        setExecutionToRequired(newRealm, keycloak, authenticationFlowRepresentation, executionReps, "reset-password");

        RealmResource resource = realmsResource.realm(newRealm);
        RealmRepresentation representation = resource.toRepresentation();
        representation.setResetCredentialsFlow(authenticationFlowRepresentation.getAlias());
        resource.update(representation);
        LOGGER.info("Set as reset credential flow: {}", authenticationFlowRepresentation.getAlias());
    }

    private void setExecutionToRequired(String newRealm, Keycloak keycloak, AuthenticationFlowRepresentation flowRepresentation,
                                        List<AuthenticationExecutionInfoRepresentation> executionReps, String FlowIdentifier) {
        AuthenticationExecutionInfoRepresentation resetPassword = findExecutionByProvider(FlowIdentifier, executionReps);
        resetPassword.setRequirement("REQUIRED");
        keycloak.realm(newRealm).flows().updateExecutions(flowRepresentation.getAlias(), resetPassword);
    }

    private AuthenticationExecutionInfoRepresentation findExecutionByProvider(String provider, List<AuthenticationExecutionInfoRepresentation> executions) {
        LOGGER.info("Provider: " + provider);
        for (AuthenticationExecutionInfoRepresentation exec : executions) {
            if (provider.equals(exec.getProviderId())) {
                return exec;
            }
        }
        return null;
    }
}
