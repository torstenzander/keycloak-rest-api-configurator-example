package de.tzander.keycloak.configurator;

import org.keycloak.representations.idm.ProtocolMapperRepresentation;

import java.util.HashMap;
import java.util.Map;

public class ProtocolMapperRepresentationFactory {

    /**
     * AttributeName is mapped to JWT
     * @param protocol
     * @return {@link ProtocolMapperRepresentation}
     */
    public static ProtocolMapperRepresentation create(String protocol){
        ProtocolMapperRepresentation protocolMapperRepresentation = new ProtocolMapperRepresentation();
        protocolMapperRepresentation.setName("AttributeName");
        protocolMapperRepresentation.setProtocol(protocol);
        protocolMapperRepresentation.setProtocolMapper("oidc-usermodel-attribute-mapper");
        Map<String, String> bknMap = new HashMap<>();
        bknMap.put("user.attribute", "AttributeName");
        bknMap.put("claim.name", "AttributeName");
        bknMap.put("id.token.claim", "true");
        bknMap.put("access.token.claim", "true");
        bknMap.put("jsonType.label", "String");
        bknMap.put("userinfo.token.claim", "true");
        protocolMapperRepresentation.setConfig(bknMap);
        return protocolMapperRepresentation;
    }
}
