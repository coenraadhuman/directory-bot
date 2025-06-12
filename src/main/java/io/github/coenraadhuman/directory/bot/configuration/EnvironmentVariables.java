package io.github.coenraadhuman.directory.bot.configuration;

import java.util.Optional;

public class EnvironmentVariables {

    private EnvironmentVariables() {
        throw new RuntimeException("This is a utility class and should not be constructed");
    }

    public static Optional<String> getVariable(Property key) {
        return Optional.ofNullable(System.getenv(key.name()));
    }

}
