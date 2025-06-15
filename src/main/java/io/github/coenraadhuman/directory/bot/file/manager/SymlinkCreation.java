package io.github.coenraadhuman.directory.bot.file.manager;

import io.github.coenraadhuman.directory.bot.configuration.Properties;
import io.github.coenraadhuman.directory.bot.execution.ExternalRenameInvoker;
import io.github.coenraadhuman.directory.bot.utility.Checksum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

import static io.github.coenraadhuman.directory.bot.configuration.Property.*;

public class SymlinkCreation {

    private static final Logger log = LoggerFactory.getLogger(SymlinkCreation.class);

    private final Properties properties;
    private final Path sourceDirectory;
    private final Path targetDirectory;
    private final Path sourceFile;

    public SymlinkCreation(Properties properties, Path sourceDirectory, Path targetDirectory, Path sourceFile) {
        this.properties = properties;
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.sourceFile = sourceFile;
    }
    
    public void create() {

        final var renameAbsolutePath = properties.getProperty(DIRECTORY_BOT_RENAME_COMMAND)
                .flatMap(command -> ExternalRenameInvoker.invoke(sourceFile.getFileName().toString(), sourceDirectory.toString(), targetDirectory.toString(), command))
                .map(Paths::get);

        if (properties.getFlagProperty(DIRECTORY_BOT_SKIP_RENAME_FAILED_FILES) && properties.getProperty(DIRECTORY_BOT_RENAME_COMMAND).isPresent() && renameAbsolutePath.isEmpty()) {
            log.error("Could not rename: {}", sourceFile);
            return;
        }

        final var symlinkAbsolutePath = renameAbsolutePath
                .orElse(Paths.get(sourceFile.getParent().toString().replace(sourceDirectory.toString(), targetDirectory.toString()), sourceFile.getFileName().toString()));

        final var symlinkRootDirectory = symlinkAbsolutePath.getParent();

        Optional<Path> clashSymlinkAbsolutePath = Optional.empty();
        
        log.info("Processing source file: {} with determined symlink: {}", sourceFile, symlinkAbsolutePath);
        
        try {
            if (!Files.exists(symlinkRootDirectory)) {
                Files.createDirectories(symlinkRootDirectory);
                log.info("Created missing root directory: {} for symlink: {}", symlinkRootDirectory, symlinkAbsolutePath);
            }

            if (Files.isSymbolicLink(symlinkAbsolutePath)) {
                log.info("Symlink: {} already exists", symlinkAbsolutePath);

                if (!Files.exists(symlinkAbsolutePath)) {
                    log.warn("Invalid symlink: {} found, removing it", symlinkAbsolutePath);
                    Files.delete(symlinkAbsolutePath);
                } else {

                    var existingSymlinkTarget = Files.readSymbolicLink(symlinkAbsolutePath);
                    var existingSymlinkTargetPath = Paths.get(existingSymlinkTarget.getParent().toString(), existingSymlinkTarget.getFileName().toString());

                    if (sourceFile.equals(existingSymlinkTargetPath)) {
                        log.info("Skipping creation of symlink: {}, already exists for source file: {}", symlinkAbsolutePath, sourceFile);
                        return;
                    } else {
                        if (properties.getFlagProperty(DIRECTORY_BOT_SYMLINK_CREATION_AVOID_VALID_OVERWRITE)) {
                            var newFilePath = Checksum.getMD5(sourceFile.toString())
                                    .map(checksum -> {
                                        var filename = symlinkAbsolutePath.getFileName().toString();
                                        int dotIndex = filename.lastIndexOf('.');

                                        if (dotIndex == -1) {
                                            log.warn("File: {} does not have an extension", sourceFile);
                                            return filename + " - " + checksum;
                                        }

                                        var extension = filename.substring(dotIndex);
                                        var baseName = filename.substring(0, dotIndex);
                                        return baseName + " - " + checksum + extension;
                                    })
                                    .map(newFileName -> Paths.get(symlinkRootDirectory.toString(), newFileName))
                                    .orElseThrow(() -> new RuntimeException("Could not create new name for symlink: {} to avoid overwrite".formatted(symlinkAbsolutePath)));

                            clashSymlinkAbsolutePath = Optional.of(newFilePath);
                        } else {
                            log.warn("Symlink: {} with incorrect target, removing it", symlinkAbsolutePath);
                            Files.delete(symlinkAbsolutePath);
                        }
                    }
                }
            }

            Files.createSymbolicLink(clashSymlinkAbsolutePath.orElse(symlinkAbsolutePath), sourceFile);
            log.info("Created symlink: {} for source file: {}", clashSymlinkAbsolutePath.orElse(symlinkAbsolutePath), sourceFile);
        } catch (IOException e) {
            log.error("Could not create symlink: {} for source file: {}", symlinkAbsolutePath, sourceFile);
        }
    }
}
