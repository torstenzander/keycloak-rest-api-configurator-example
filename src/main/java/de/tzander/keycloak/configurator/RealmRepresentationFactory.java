package de.tzander.keycloak.configurator;

import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RealmRepresentationFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealmRepresentationFactory.class);

    public static RealmRepresentation create(String newRealm, String theme, String displayName, String passwordPolicy) {

        AuthenticatorConfigRepresentation configRepresentation = new AuthenticatorConfigRepresentation();
        List<AuthenticatorConfigRepresentation> configList = new ArrayList<>();
        configList.add(configRepresentation);

        RealmRepresentation realmRepresentation = new RealmRepresentation();
        realmRepresentation.setRealm(newRealm);
        realmRepresentation.setEnabled(true);
        realmRepresentation.setLoginTheme(theme);
        realmRepresentation.setEmailTheme(theme);
        realmRepresentation.setDisplayName(displayName);
        realmRepresentation.setDefaultLocale("de");
        realmRepresentation.setResetPasswordAllowed(true);
        realmRepresentation.setLoginWithEmailAllowed(false);
        realmRepresentation.setRegistrationAllowed(false);
        realmRepresentation.setEditUsernameAllowed(false);
        realmRepresentation.setResetPasswordAllowed(true);
        realmRepresentation.setRememberMe(false);
        realmRepresentation.setVerifyEmail(false);
        realmRepresentation.setLoginWithEmailAllowed(false);
        realmRepresentation.setDuplicateEmailsAllowed(false);
        realmRepresentation.setSslRequired("none");
        realmRepresentation.setAuthenticatorConfig(configList);
        realmRepresentation.setBrowserFlow("browser");
        realmRepresentation.setRegistrationFlow("registration");
        realmRepresentation.setEventsEnabled(true);
        realmRepresentation.setAdminEventsEnabled(true);
        realmRepresentation.setAdminEventsDetailsEnabled(true);
        realmRepresentation.setInternationalizationEnabled(true);
        Set<String> set = new HashSet<>();
        set.add("de");
        realmRepresentation.setSupportedLocales(set);
        List<String> eventTypes = new ArrayList<>();
        eventTypes.add("SEND_RESET_PASSWORD");
        eventTypes.add("LOGIN_ERROR");
        eventTypes.add("CLIENT_LOGIN");
        eventTypes.add("RESET_PASSWORD_ERROR");
        eventTypes.add("IMPERSONATE_ERROR");
        realmRepresentation.setEnabledEventTypes(eventTypes);
        realmRepresentation.setPasswordPolicy(passwordPolicy);

        Map<String, String> map = new HashMap<>();
        map.put("host", "smtp.example.de");
        map.put("from", "no-reply@example.de");
        map.put("fromDisplayName", "REALM COMPANY");
        realmRepresentation.setSmtpServer(map);
        return realmRepresentation;
    }

}
