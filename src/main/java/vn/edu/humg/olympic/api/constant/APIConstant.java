package vn.edu.humg.olympic.api.constant;

public final class APIConstant {
    private static final String COMMON_PATH = "/api";
    private static final String API_VERSION = "/v1";
    public static final String API_BASE_PATH = COMMON_PATH + API_VERSION;

    public static final String REFRESH_TOKEN_NAME = "refresh_token";
    public static final String ACCESS_TOKEN_NAME = "access_token";

    public static final String API_AUTH_PATH = API_BASE_PATH + "/auth";

    public static final String REGISTER = "/register";

    public static final String LOGIN = "/login";

    public static final String LOGOUT = "/logout";

    public static final String REFRESH_TOKEN = "/refresh-token";

    public static final String[] API_AUTH_PATHS = {API_AUTH_PATH + REGISTER, API_AUTH_PATH + LOGIN,
            API_AUTH_PATH + LOGOUT, API_AUTH_PATH + REFRESH_TOKEN};

    private APIConstant() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }
}
