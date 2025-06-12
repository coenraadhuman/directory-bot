package io.github.coenraadhuman.directory.bot.file.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExistingTargetFileCorrector {

    private static final Logger log = LoggerFactory.getLogger(ExistingTargetFileCorrector.class);

    private final Path sourceDirectory;
    private final Path targetDirectory;
    private final Path existingTargetFile;

    public ExistingTargetFileCorrector(Path sourceDirectory, Path targetDirectory, Path existingTargetFile) {
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.existingTargetFile = existingTargetFile;
    }

    public void move() {
        final var correctedRootDirectory = Paths.get(existingTargetFile.getParent().toString().replace(targetDirectory.toString(), sourceDirectory.toString()));
        final var correctedAbsolutePath = Paths.get(correctedRootDirectory.toString(), existingTargetFile.getFileName().toString());

        if (!Files.isSymbolicLink(existingTargetFile) && Files.isRegularFile(existingTargetFile)) {
            log.info("Processing existing file: {} in target directory that should be moved to source directory: {}", existingTargetFile, correctedAbsolutePath);

            try {
                if (!Files.exists(correctedRootDirectory)) {
                    Files.createDirectories(correctedRootDirectory);
                    log.info("Created missing root directory: {} for existing file: {} in target directory move", correctedRootDirectory, existingTargetFile.getFileName());
                }

                Files.move(existingTargetFile, correctedAbsolutePath);
            } catch (IOException e) {
                log.error("Could not move file: {} in target directory to source directory: {}", existingTargetFile, correctedAbsolutePath);
            }
        }
    }
}
