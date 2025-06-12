package io.github.coenraadhuman.directory.bot.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static io.github.coenraadhuman.directory.bot.configuration.Property.*;

public class ConfigurationLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationLoader.class);

    // Todo: finalise this, correlates to the docker image.
    private static final String APPLICATION_CONFIG_PATH = "";

    private ConfigurationLoader() {
        throw new RuntimeException("This is a utility class and should not be constructed");
    }

    public static Properties retrieveApplicationProperties() {
        var properties = readFileProperties();
        putDefaults(properties);
        return properties;
    }

    private static void putDefaults(Properties properties) {
        properties.getProperty(DATABASE_CONNECTION)
                  .ifPresentOrElse(databaseConnection -> log.info("Database connection: {} value will be used and database directory will be ignored.", databaseConnection),
                        () -> properties.getProperty(DATABASE_DIRECTORY).ifPresent(
                            databaseDirectory -> {
                                final var determinedDatabaseConnection = "jdbc:sqlite:%s/directory-bot.db".formatted(databaseDirectory);
                                properties.put(DATABASE_CONNECTION, determinedDatabaseConnection);
                                log.info("Determined database connection: {}", determinedDatabaseConnection);
                        }));
    }

    private static Properties readFileProperties() {
        var properties = new Properties();
        try (InputStream input = ConfigurationLoader.class.getClassLoader().getResourceAsStream("directory-bot.properties")) { //new FileInputStream("%sdirectory-bot.properties".formatted(APPLICATION_CONFIG_PATH))) {
            properties.load(input);
        } catch (IOException ignored) {
            log.error("Could not load directory-bot properties from file.");
        }
        return properties;
    }

}
