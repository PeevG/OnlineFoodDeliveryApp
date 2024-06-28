package yummydelivery.server.config;

import java.util.List;

public class ApplicationConstants {
    private ApplicationConstants() {}
    public static final String API_BASE = "/api/v1";
    public static final List<String> CROSS_ORIGIN_DOMAINS = List.of(
            "http://localhost:3000",
            "http://localhost:4200",
            "http://127.0.0.1:80"
    );
    public static final List<String> DO_NOT_FILTER_PATHS = List.of(
            ApplicationConstants.API_BASE + "/auth/login",
            ApplicationConstants.API_BASE + "/auth/register"
    );
}
