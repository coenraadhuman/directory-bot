package io.github.coenraadhuman.directory.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

public class ApplicationProperties {

    private static final Logger log = LoggerFactory.getLogger(ApplicationProperties.class);

    // Todo: finalise this, correlates to the docker image.
    private static final String APPLICATION_CONFIG_PATH = "";

    public static final String DATABASE_DIRECTORY = "database.directory";
    public static final String DATABASE_CONNECTION = "database.connection";

    private ApplicationProperties() {
        throw new RuntimeException("This is a utility class and should not be constructed");
    }

    public static Properties retrieveApplicationProperties() {
        var properties = readFileProperties();
        readEnvironmentVariables(properties);
        putDefaults(properties);
        return properties;
    }

    private static void putDefaults(Properties properties) {
        // Todo validation on path, does it exist?
        Optional.ofNullable(properties.getProperty(DATABASE_DIRECTORY))
                .ifPresentOrElse(databaseDirectory -> properties.put(DATABASE_CONNECTION, "jdbc:sqlite:%s/directory-bot.db".formatted(databaseDirectory)),
                        () -> { throw new RuntimeException("Require database directory to be provided, either via environment variables or properties file"); });
    }

    private static void readEnvironmentVariables(Properties properties) {
        // Todo: Maybe just extend the properties have optional instead of exceptions.
        Optional.ofNullable(System.getenv("DATABASE_DIRECTORY"))
                .ifPresent(databaseDirectory -> properties.putIfAbsent(DATABASE_DIRECTORY, databaseDirectory));
    }

    private static Properties readFileProperties() {
        var properties = new Properties();
        try (InputStream input = ApplicationProperties.class.getClassLoader().getResourceAsStream("directory-bot.properties")) { //new FileInputStream("%sdirectory-bot.properties".formatted(APPLICATION_CONFIG_PATH))) {
            properties.load(input);
        } catch (IOException ignored) {
            log.error("Could not load directory-bot properties from file.");
        }
        return properties;
    }

}
