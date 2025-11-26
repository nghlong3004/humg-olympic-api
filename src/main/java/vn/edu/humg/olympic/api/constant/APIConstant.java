package vn.edu.humg.olympic.api.constant;

public final class APIConstant {
  private static final String COMMON_PATH = "/api";
  private static final String API_VERSION = "/v1";
  public static final String API_BASE_PATH = COMMON_PATH + API_VERSION;
  // User
  public static final String API_USER_PATH = API_BASE_PATH + "/user";
  // Assignment
  public static final String API_ASSIGNMENT_PATH = API_BASE_PATH + "/assignment";
  // Auth
  public static final String API_AUTH_PATH = API_BASE_PATH + "/auth";
  public static final String[] API_AUTH_PATHS = {
    API_AUTH_PATH + "/register",
    API_AUTH_PATH + "/login",
    API_AUTH_PATH + "/logout",
    API_AUTH_PATH + "/refresh-token"
  };

  public static final String REFRESH_TOKEN_NAME = "refresh_token";
  public static final String ACCESS_TOKEN_NAME = "access_token";

  private APIConstant() {
    throw new UnsupportedOperationException("This class should never be instantiated");
  }
}
