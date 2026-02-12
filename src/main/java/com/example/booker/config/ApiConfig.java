package com.example.booker.config;

public final class ApiConfig {

    private ApiConfig() {
    }

    public static String baseUrl() {
        return resolve("booker.baseUrl", "BOOKER_BASE_URL", "https://restful-booker.herokuapp.com");
    }

    public static String username() {
        return resolve("booker.username", "BOOKER_USERNAME", null);
    }

    public static String password() {
        return resolve("booker.password", "BOOKER_PASSWORD", null);
    }

    private static String resolve(String sysProp, String envVar, String defaultValue) {
        String value = System.getProperty(sysProp);
        if (value != null) {
            return value;
        }
        value = System.getenv(envVar);
        if (value != null) {
            return value;
        }
        if (defaultValue != null) {
            return defaultValue;
        }
        throw new IllegalStateException(
                "Required config missing: set system property '" + sysProp + "' or environment variable '" + envVar + "'");
    }
}
