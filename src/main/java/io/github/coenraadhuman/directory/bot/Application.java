package io.github.coenraadhuman.directory.bot;

import io.github.coenraadhuman.directory.bot.configuration.ConfigurationLoader;
import io.github.coenraadhuman.directory.bot.execution.ExecutionManager;
import io.github.coenraadhuman.directory.bot.file.manager.ExistingTargetFileCorrector;
import io.github.coenraadhuman.directory.bot.file.manager.SymlinkCleaner;
import io.github.coenraadhuman.directory.bot.file.manager.SymlinkCreation;
import io.github.coenraadhuman.directory.bot.filebot.FilebotActivate;
import io.github.coenraadhuman.directory.bot.persistence.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static io.github.coenraadhuman.directory.bot.configuration.Property.*;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args)  {
        var properties = ConfigurationLoader.retrieveApplicationProperties();

        if (properties.getFlagProperty(DIRECTORY_BOT_FILEBOT_RENAME_ENABLE)) {
            FilebotActivate.activate(properties);
        }

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
            var extensionsToSkip = properties.getProperty(DIRECTORY_BOT_SKIP_FILE_EXTENSIONS).map(value -> value.split(","));

            try (var sourcePaths = Files.walk(sourceDirectory)) {
                sourcePaths
                        .filter(path -> !path.toFile().isDirectory())
                        .filter(path -> skipExtension(extensionsToSkip, path))
                        .forEach(sourceFile -> new SymlinkCreation(jdbi, properties, sourceDirectory, targetDirectory, sourceFile).create());
            } catch (IOException e) {
                // Do nothing for now
            }

            // Todo: can be configured
            try (var targetPaths = Files.walk(targetDirectory)) {
                targetPaths
                        .filter(path -> !path.toFile().isDirectory())
                        .filter(path -> skipExtension(extensionsToSkip, path))
                        // Will just move and with next run symlink will be created
                        .forEach(targetFile -> new ExistingTargetFileCorrector(sourceDirectory, targetDirectory, targetFile).move());
            } catch (IOException e) {
                // Do nothing for now
            }

            try (var targetPaths = Files.walk(targetDirectory)) {
                targetPaths
                        .filter(path -> !path.toFile().isDirectory())
                        .filter(path -> skipExtension(extensionsToSkip, path))
                        // Will just move and with next run symlink will be created
                        .forEach(targetFile -> new SymlinkCleaner(targetFile).clean());
            } catch (IOException e) {
                // Do nothing for now
            }
        });
    }

    private static boolean skipExtension(Optional<String[]> extensionsToSkip, Path sourceFile) {
        if (extensionsToSkip.isPresent()) {
            for (var extension : extensionsToSkip.get()) {
                if (sourceFile.getFileName().toString().endsWith(extension)) {
                    log.info("Skipping extension: {} for file: {}", extension, sourceFile);
                    return false;
                }
            }
        }
        return true;
    }


}