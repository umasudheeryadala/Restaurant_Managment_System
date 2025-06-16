package com.tastes_of_india.restaurantManagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application" ,ignoreUnknownFields = false)
public class ApplicationProperties {

    String keycloak_access_token_url;
    String keycloak_password_update_url;
    String keycloak_admin_user_name;
    String keycloak_admin_password;
    String keycloak_client_id;
    String keycloak_client_secret;
    String grant_type;

    String clientSecret;

    public String getKeycloak_access_token_url() {
        return keycloak_access_token_url;
    }

    public void setKeycloak_access_token_url(String keycloak_access_token_url) {
        this.keycloak_access_token_url = keycloak_access_token_url;
    }

    public String getKeycloak_password_update_url() {
        return keycloak_password_update_url;
    }

    public void setKeycloak_password_update_url(String keycloak_password_update_url) {
        this.keycloak_password_update_url = keycloak_password_update_url;
    }

    public String getKeycloak_admin_user_name() {
        return keycloak_admin_user_name;
    }

    public void setKeycloak_admin_user_name(String keycloak_admin_user_name) {
        this.keycloak_admin_user_name = keycloak_admin_user_name;
    }

    public String getKeycloak_admin_password() {
        return keycloak_admin_password;
    }

    public void setKeycloak_admin_password(String keycloak_admin_password) {
        this.keycloak_admin_password = keycloak_admin_password;
    }

    public String getKeycloak_client_id() {
        return keycloak_client_id;
    }

    public void setKeycloak_client_id(String keycloak_client_id) {
        this.keycloak_client_id = keycloak_client_id;
    }

    public String getKeycloak_client_secret() {
        return keycloak_client_secret;
    }

    public void setKeycloak_client_secret(String keycloak_client_secret) {
        this.keycloak_client_secret = keycloak_client_secret;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
