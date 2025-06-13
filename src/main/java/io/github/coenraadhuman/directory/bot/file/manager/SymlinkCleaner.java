package io.github.coenraadhuman.directory.bot.file.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SymlinkCleaner {

    private static final Logger log = LoggerFactory.getLogger(ExistingTargetFileCorrector.class);

    private final Path targetFile;

    public SymlinkCleaner(Path targetFile) {
        this.targetFile = targetFile;
    }

    public void clean () {
        if (Files.isSymbolicLink(targetFile) && !Files.exists(targetFile)) {
            log.info("Found invalid symlink: {} removing it", targetFile);

            try {
                Files.delete(targetFile);
            } catch (IOException e) {
                log.error("Could not remove invalid symlink: {}", targetFile);
            }
        }
    }

}
