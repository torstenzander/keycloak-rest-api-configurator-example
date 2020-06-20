package de.tzander.keycloak.configurator;

import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientScopeRepresentationFactory {

    /**
     * Creates a client scope with a custom attribute
     *
     * @param mappers
     * @return {@link ClientScopeRepresentation}
     */
    public static ClientScopeRepresentation create(List<ProtocolMapperRepresentation> mappers){
        ClientScopeRepresentation clientScopeRepresentation = new  ClientScopeRepresentation();
        clientScopeRepresentation.setProtocol("openid-connect");
        clientScopeRepresentation.setName("CustomAttributeForToken");
        Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put("include.in.token.scope", "true");
        clientScopeRepresentation.setAttributes(attributeMap);
        clientScopeRepresentation.setProtocolMappers(mappers);
        return clientScopeRepresentation;
    }
}
