package io.github.coenraadhuman.directory.bot.file.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SymlinkCreation {

    private static final Logger log = LoggerFactory.getLogger(SymlinkCreation.class);
    
    private final Path sourceDirectory;
    private final Path targetDirectory;
    private final Path sourceFile;

    public SymlinkCreation(Path sourceDirectory, Path targetDirectory, Path sourceFile) {
        this.sourceDirectory = sourceDirectory;
        this.targetDirectory = targetDirectory;
        this.sourceFile = sourceFile;
    }
    
    public void create() {
        final var symlinkRootDirectory = Paths.get(sourceFile.getParent().toString().replace(sourceDirectory.toString(), targetDirectory.toString()));
        // Todo: this second argument might undergo a rename if user stipulates it:
        final var symlinkAbsolutePath = Paths.get(symlinkRootDirectory.toString(), sourceFile.getFileName().toString());
        
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
                    }

                    // Todo: configuration for how to handle multiple sources with same file name and directory?
                    log.warn("Symlink: {} with incorrect target, removing it", symlinkAbsolutePath);
                    Files.delete(symlinkAbsolutePath);
                }
            }

            Files.createSymbolicLink(symlinkAbsolutePath, sourceFile);
            log.info("Created symlink: {} for source file: {}", symlinkAbsolutePath, sourceFile);
        } catch (IOException e) {
            log.error("Could not create symlink: {} for source file: {}", symlinkAbsolutePath, sourceFile);
        }
    }
}
