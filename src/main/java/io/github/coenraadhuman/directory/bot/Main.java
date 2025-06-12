package io.github.coenraadhuman.directory.bot;

import io.github.coenraadhuman.directory.bot.configuration.ConfigurationLoader;
import io.github.coenraadhuman.directory.bot.file.manager.SymlinkCreation;
import io.github.coenraadhuman.directory.bot.persistence.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.github.coenraadhuman.directory.bot.configuration.Property.*;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)  {
        var properties = ConfigurationLoader.retrieveApplicationProperties();

        var database = new Database(properties
                .getProperty(DIRECTORY_BOT_DATABASE_CONNECTION)
                .orElseThrow(() -> new RuntimeException("Require database directory to be provided, either via environment variables or properties file"))
        );

        database.migrate();
        var jdbi = database.connection();

        // Todo: exceptions
        final var sourceDirectory = properties.getProperty(DIRECTORY_BOT_SOURCE_DIRECTORY).map(Paths::get).orElseThrow();
        final var targetDirectory = properties.getProperty(DIRECTORY_BOT_TARGET_DIRECTORY).map(Paths::get).orElseThrow();

        try (var sourcePaths = Files.walk(sourceDirectory)) {
            sourcePaths
                .filter(path -> !path.toFile().isDirectory())
                .forEach(sourceFile -> new SymlinkCreation(sourceDirectory, targetDirectory, sourceFile).create());
        } catch (IOException e) {
            // Do nothing for now
        }
    }


}