package de.tzander.keycloak.configurator;

import org.keycloak.representations.idm.ClientRepresentation;

import java.util.ArrayList;
import java.util.List;

public class ClientRepresentationFactory {

    public static ClientRepresentation create(String newClient, String redirectUrl, String protocol) {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setName(newClient);
        clientRepresentation.setId(newClient);
        clientRepresentation.setDirectAccessGrantsEnabled(true);
        clientRepresentation.setEnabled(true);
        clientRepresentation.setProtocol(protocol);
        clientRepresentation.setStandardFlowEnabled(true);
        clientRepresentation.setImplicitFlowEnabled(true);
        List<String> urls = new ArrayList<>();
        urls.add(redirectUrl);
        clientRepresentation.setRedirectUris(urls);
        return clientRepresentation;
    }

}