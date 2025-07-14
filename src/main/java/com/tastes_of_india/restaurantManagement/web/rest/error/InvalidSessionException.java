package com.tastes_of_india.restaurantManagement.web.rest.error;

public class InvalidSessionException extends Exception{

    private final String entityName;

    private final String errorKey;

    private final String defaultMessage;

    public InvalidSessionException(String defaultMessage,String entityName, String errorKey) {
        this.entityName = entityName;
        this.errorKey = errorKey;
        this.defaultMessage=defaultMessage;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
