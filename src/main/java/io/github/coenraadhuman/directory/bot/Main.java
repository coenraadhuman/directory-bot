package io.github.coenraadhuman.directory.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)  {
        var properties = ApplicationProperties.retrieveApplicationProperties();
        Database.migrate(properties);
        var jdbi = Database.retrieveDatabaseConnection(properties);

        // Start never ending process that monitors primary directory, what about target directory?

        var sourceRootDirectory = Paths.get("/Users/coenraadhuman/Development/personal/directory-bot-test/a");
        var targetRootDirectory = Paths.get("/Users/coenraadhuman/Development/personal/directory-bot-test/b");

        try (var sourcePaths = Files.walk(sourceRootDirectory)) {
            sourcePaths
                .filter(path -> !path.toFile().isDirectory())
                .forEach(sourcePath -> {
                    log.info("Found source path: {}", sourcePath);
                    log.info("Parent path: {}", sourcePath.getParent());
                    log.info("File name: {}", sourcePath.getFileName());
                    var targetDeterminedDirectory = Paths.get(sourcePath.getParent().toString().replace(sourceRootDirectory.toString(), targetRootDirectory.toString()));
                    var targetPath = Paths.get(targetDeterminedDirectory.toString(), sourcePath.getFileName().toString());
                    log.info("Determined target: {}", targetPath);
                    try {
                        if (!Files.exists(targetDeterminedDirectory)) {
                           Files.createDirectories(targetDeterminedDirectory);
                        }
                        if (Files.isSymbolicLink(targetPath)) {
                            if (!Files.exists(targetPath)) {
                                log.warn("Found symbolic link with missing target");
                                Files.delete(targetPath);
                                Files.createSymbolicLink(targetPath, sourcePath);
                                log.info("Updated symbolic link: {} with source: {}", targetPath, sourcePath);
                            } else {
                                var existingSymbolicLinkSourceFile = Files.readSymbolicLink(targetPath);
                                var existingSymbolicLinkSourcePath = Paths.get(existingSymbolicLinkSourceFile.getParent().toString(), existingSymbolicLinkSourceFile.getFileName().toString());
                                log.info("Symbolic link already exists for target: {}", targetPath);
                                if (sourcePath.equals(existingSymbolicLinkSourcePath)) {
                                    log.info("Skipping creating symbolic link target: {} already exists for source: {}", targetPath, sourcePath);
                                } else {
                                    Files.delete(targetPath);
                                    Files.createSymbolicLink(targetPath, sourcePath);
                                    log.info("Updated symbolic link: {} with source: {}", targetPath, sourcePath);
                                }
                            }
                        } else {
                            Files.createSymbolicLink(targetPath, sourcePath);
                            log.info("Created symbolic link: {} for source: {}", targetPath, sourcePath);
                        }
                    } catch (IOException e) {
                        log.error("Could not create symbolic link for {} because {}", targetPath, e.getMessage());
                    }
                }
            );
        } catch (IOException e) {
            // Do nothing for now
        }
    }


}