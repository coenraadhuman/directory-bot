package io.github.coenraadhuman.directory.bot.configuration;

public enum Property {

    DATABASE_DIRECTORY("database.directory"),
    DATABASE_CONNECTION("database.connection"),
    SOURCE_DIRECTORY("source.directory"),
    TARGET_DIRECTORY("target.directory");

    private final String propertyValue;

    Property(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public String toString() {
        return propertyValue;
    }

}
