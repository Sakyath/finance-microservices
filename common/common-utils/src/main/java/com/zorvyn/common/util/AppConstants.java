package com.zorvyn.common.util;

public final class AppConstants {

    private AppConstants() {}

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_HEADER = "X-User-Role";
    public static final String USER_ID_HEADER = "X-User-Id";
    public static final String USERNAME_HEADER = "X-Username";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_ANALYST = "ROLE_ANALYST";
    public static final String ROLE_VIEWER = "ROLE_VIEWER";

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    public static final int JWT_EXPIRY_HOURS = 24;
}
