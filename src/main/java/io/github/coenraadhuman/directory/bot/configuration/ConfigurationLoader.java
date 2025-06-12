package io.github.coenraadhuman.directory.bot.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static io.github.coenraadhuman.directory.bot.configuration.Property.*;

public class ConfigurationLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationLoader.class);

    private ConfigurationLoader() {
        throw new RuntimeException("This is a utility class and should not be constructed");
    }

    public static Properties retrieveApplicationProperties() {
        var properties = new Properties();

        putRequired(properties);
        putDefaults(properties);
        readFileProperties(properties);

        return properties;
    }

    private static void putRequired(Properties properties) {
        Optional.ofNullable(System.getenv(DIRECTORY_BOT_CONFIGURATION_DIRECTORY.name()))
                .ifPresentOrElse(configurationDirectory -> {
                    log.info("Configuration directory detected: {} ", configurationDirectory);
                    properties.put(DIRECTORY_BOT_CONFIGURATION_DIRECTORY, configurationDirectory);
                }, () -> {
                    log.warn("Configuration not provided defaulting to: /config");
                    properties.put(DIRECTORY_BOT_CONFIGURATION_DIRECTORY, "/config");
                });
    }

    private static void putDefaults(Properties properties) {
        Optional.ofNullable(System.getenv(DIRECTORY_BOT_CONFIGURATION_DIRECTORY.name()))
                .ifPresentOrElse(configurationDirectory -> {
                    final var determinedDatabaseConnection = "jdbc:sqlite:%s/directory-bot.db".formatted(configurationDirectory);
                    properties.put(DIRECTORY_BOT_DATABASE_CONNECTION, determinedDatabaseConnection);
                    log.info("Determined database connection: {}", determinedDatabaseConnection);
                }, () -> {
                    throw new RuntimeException("Configuration directory is not known");
                });
    }

    private static void readFileProperties(Properties properties) {
        properties.getProperty(DIRECTORY_BOT_CONFIGURATION_DIRECTORY)
                .map(Path::of)
                .filter(Files::exists)
                .map(Path::toString)
                .map("%s/directory-bot.properties"::formatted)
                .ifPresentOrElse(configurationPath -> {
                    try (InputStream input = new FileInputStream(configurationPath)) {
                        properties.load(input);
                        log.info("Ingested configuration file: {}", configurationPath);
                    } catch (IOException ignored) {
                        log.error("Could not load directory-bot properties from file: {}", configurationPath);
                    }
                }, () -> log.warn("No configuration file provided"));
    }

}
