package io.github.coenraadhuman.directory.bot;

import io.github.coenraadhuman.directory.bot.configuration.ConfigurationLoader;
import io.github.coenraadhuman.directory.bot.execution.manager.ExecutionManager;
import io.github.coenraadhuman.directory.bot.file.manager.ExistingTargetFileCorrector;
import io.github.coenraadhuman.directory.bot.file.manager.SymlinkCleaner;
import io.github.coenraadhuman.directory.bot.file.manager.SymlinkCreation;
import io.github.coenraadhuman.directory.bot.persistence.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.github.coenraadhuman.directory.bot.configuration.Property.*;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args)  {
        var properties = ConfigurationLoader.retrieveApplicationProperties();

        var database = new Database(properties
                .getProperty(DIRECTORY_BOT_DATABASE_CONNECTION)
                .orElseThrow(() -> new RuntimeException("Require database directory to be provided, either via environment variables or properties file"))
        );

        database.migrate();
        var jdbi = database.connection();

        final var sourceDirectory = properties.getProperty(DIRECTORY_BOT_SOURCE_DIRECTORY).map(Paths::get)
                .orElseThrow(() -> new RuntimeException("Please provide source directory"));

        final var targetDirectory = properties.getProperty(DIRECTORY_BOT_TARGET_DIRECTORY).map(Paths::get)
                .orElseThrow(() -> new RuntimeException("Please provide target directory"));

        ExecutionManager.execute(properties, () -> {
            try (var sourcePaths = Files.walk(sourceDirectory)) {
                sourcePaths
                        .filter(path -> !path.toFile().isDirectory())
                        .forEach(sourceFile -> new SymlinkCreation(properties, sourceDirectory, targetDirectory, sourceFile).create());
            } catch (IOException e) {
                // Do nothing for now
            }

            // Todo: can be configured
            try (var targetPaths = Files.walk(targetDirectory)) {
                targetPaths
                        .filter(path -> !path.toFile().isDirectory())
                        // Will just move and with next run symlink will be created
                        .forEach(targetFile -> new ExistingTargetFileCorrector(sourceDirectory, targetDirectory, targetFile).move());
            } catch (IOException e) {
                // Do nothing for now
            }

            try (var targetPaths = Files.walk(targetDirectory)) {
                targetPaths
                        .filter(path -> !path.toFile().isDirectory())
                        // Will just move and with next run symlink will be created
                        .forEach(targetFile -> new SymlinkCleaner(targetFile).clean());
            } catch (IOException e) {
                // Do nothing for now
            }
        });
    }


}