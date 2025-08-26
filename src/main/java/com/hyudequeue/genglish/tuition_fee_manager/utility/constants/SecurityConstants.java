package com.hyudequeue.genglish.tuition_fee_manager.utility.constants;

public class SecurityConstants {
    public static final String[] ALLOWED_ORIGINS = {
            "http://localhost:2707",
            "https://genglish.threemusketeer.click",
            "https://genglish-internal.threemusketeer.click",
            "http://localhost:1600",
            "https://portal-internal.gsenglish.org",
            "https://portal.gsenglish.org",
    };
    public static final long CORS_MAX_AGE = 3600;

    public static final String SESSION_COOKIE = "JSESSIONID";
    public static final String LOGIN_SUCCESS_URL = "/login?logout";
    public static final String LOGOUT_URL = "/logout";
    public static final String[] PUBLIC_URLS = {
            "/oauth2/authorization/google",
            "/oauth2/authorization/github",
            "/login/oauth2/code/*",
            "/grantcode",
            "/api/v1/login/non-type",
            "/api/auth/**",
            "/api/auth/*",
            "/api/user/v1/**",
            "/v2/api-docs/**",
            "/v3/api-docs",
            "/v3/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/index.html#/**",
            "/swagger-ui/index.html/**"
    };
    public static final String ACCESS_DENIED_PAGE = "/access-denied";
    public static final String TEACHER_URL_PREFIX = "/teacher/**";
    public static final String STUDENT_URL_PREFIX = "/student/**";
    public static final String ROLE_TEACHER = "TEACHER";
    public static final String ROLE_STUDENT = "STUDENT";
}
