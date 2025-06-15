package io.github.coenraadhuman.directory.bot.execution;

import io.github.coenraadhuman.directory.bot.utility.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExternalRenameInvoker {

    private static final Logger log = LoggerFactory.getLogger(ExternalRenameInvoker.class);

    private ExternalRenameInvoker() {
    }

    public static Optional<String> invoke(String fileAbsolutePath, String fileName, String sourceDirectory, String targetDirectory, String processArgs) {
        try {
            if (processArgs.isBlank()) {
                log.error("No rename bash script provided.");
                return Optional.empty();
            }

            if (!processArgs.contains(".sh")) {
                log.error("Bash script not provided for rename, requires bash script.");
                return Optional.empty();
            }

            var processArgsWithArguments = Arrays.stream(processArgs.split(" ")).collect(Collectors.toCollection(ArrayList::new));
            processArgsWithArguments.add(fileAbsolutePath);
            processArgsWithArguments.add(fileName);
            processArgsWithArguments.add(sourceDirectory);
            processArgsWithArguments.add(targetDirectory);

            var processBuilder = new ProcessBuilder(processArgsWithArguments);
            var process = processBuilder.start();

            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            List<String> lines = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    lines.add(line);
                }
            }

            int exitCode = process.waitFor();

            if (exitCode == 0 && lines.size() == 1) {
                return Optional.of(lines.getFirst());
            }
        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.empty();
    }
}

