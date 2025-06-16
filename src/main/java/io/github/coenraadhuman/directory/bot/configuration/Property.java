package io.github.coenraadhuman.directory.bot.configuration;

import java.util.Optional;

public enum Property {

    DIRECTORY_BOT_CONFIGURATION_DIRECTORY("configuration.directory", "/config"),
    DIRECTORY_BOT_DATABASE_CONNECTION("database.connection", null), // Used internally
    DIRECTORY_BOT_SINGLE_EXECUTION("single.execution", "false"),
    DIRECTORY_BOT_SYMLINK_CREATION_AVOID_VALID_OVERWRITE("symlink.creation.avoid.valid.overwrite", "true"),
    DIRECTORY_BOT_FILEBOT_RENAME_ENABLE("filebot.rename.enable", "false"),
    DIRECTORY_BOT_FILEBOT_RENAME_FORMAT("filebot.rename.format", "{ plex.id }"),
    DIRECTORY_BOT_FILEBOT_SKIP_RENAME_FAILED_FILES("filebot.skip.rename.failed.files", "true"),
    DIRECTORY_BOT_SOURCE_DIRECTORY("source.directory", null),
    DIRECTORY_BOT_TARGET_DIRECTORY("target.directory", null);

    private final String propertyValue;
    private final String defaultValue;

    Property(String propertyValue, String defaultValue) {
        this.propertyValue = propertyValue;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return propertyValue;
    }

    public Optional<String> defaultValue() {
        return Optional.ofNullable(defaultValue);
    }

}
