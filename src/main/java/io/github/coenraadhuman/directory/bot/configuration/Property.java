package io.github.coenraadhuman.directory.bot.configuration;

public enum Property {

    DIRECTORY_BOT_CONFIGURATION_DIRECTORY("configuration.directory"),
    DIRECTORY_BOT_DATABASE_CONNECTION("database.connection"), // Used internally
    DIRECTORY_BOT_SOURCE_DIRECTORY("source.directory"),
    DIRECTORY_BOT_TARGET_DIRECTORY("target.directory");

    private final String propertyValue;

    Property(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public String toString() {
        return propertyValue;
    }

}
