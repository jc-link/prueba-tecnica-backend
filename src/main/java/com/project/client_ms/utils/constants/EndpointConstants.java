package com.project.client_ms.utils.constants;

public class EndpointConstants {

    public static final String ENDPOINT_BASE_API = "/api";
    public static final String ENDPOINT_AUTH = ENDPOINT_BASE_API + "/auth";
    public static final String ENDPOINT_AUTH_PATTERN = ENDPOINT_AUTH + "/**";
    public static final String ENDPOINT_CLIENTS = ENDPOINT_BASE_API + "/clients";
    public static final String ENDPOINT_CLIENTS_PATTERN = ENDPOINT_CLIENTS + "/**";
    public static final String ENDPOINT_LOGOUT = ENDPOINT_AUTH + "/logout";

}
