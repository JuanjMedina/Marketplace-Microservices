package com.marketplace.userservice.utils;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;

public class KeycloakProvider {

    private static final String SERVER_URL = "http://localhost:8085";
    private static final String REALM_NAME = "marketplace";
    private static final String REALM_MASTER = "master";
    private static final String ADMIN_CLI = "admin-cli";
    private static final String ADMIN_USER_CONSOLE = "admin";
    private static final String ADMIN_USER_PASSWORD = "admin";
    private static final String CLIEN_SECRET = "SeqiQTR6Sp8N5vpNluazHbrVvJgnozp1";

    public static RealmResource getRealmResource() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_MASTER)
                .username(ADMIN_USER_CONSOLE)
                .password(ADMIN_USER_PASSWORD)
                .clientId(ADMIN_CLI)
                .clientSecret(CLIEN_SECRET)
                .grantType("password")
                // max connections to the Keycloak server with 10 connections in the pool
                .resteasyClient(new ResteasyClientBuilderImpl().connectionPoolSize(10).build())
                .build();

        return keycloak.realm(REALM_NAME); // Accedemos al realm marketplace
    }

    public static UsersResource getUserResource() {
        RealmResource realmResource = getRealmResource();
        return realmResource.users();
    }
}
