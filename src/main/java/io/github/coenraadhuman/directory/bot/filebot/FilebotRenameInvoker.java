package io.github.coenraadhuman.directory.bot.filebot;

import io.github.coenraadhuman.directory.bot.configuration.Properties;
import io.github.coenraadhuman.directory.bot.configuration.Property;
import io.github.coenraadhuman.directory.bot.utility.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static io.github.coenraadhuman.directory.bot.configuration.Property.DIRECTORY_BOT_FILEBOT_RENAME_ENABLE;
import static io.github.coenraadhuman.directory.bot.configuration.Property.DIRECTORY_BOT_FILEBOT_RENAME_FORMAT;

public class FilebotRenameInvoker {

    private static final Logger log = LoggerFactory.getLogger(FilebotRenameInvoker.class);

    private static final Pattern FROM_TO_PATTERN = Pattern.compile("from \\[(.+)] to \\[(.+)]");
    private static final Pattern SKIPPED_BECAUSE_PATTERN = Pattern.compile("Skipped \\[(.+)] because \\[(.+)]");

    private FilebotRenameInvoker() {
    }

    public static Optional<String> invoke(Properties properties, String filePath, String targetDirectory) {
        if (!properties.getFlagProperty(DIRECTORY_BOT_FILEBOT_RENAME_ENABLE) || properties.getProperty(DIRECTORY_BOT_FILEBOT_RENAME_FORMAT).isEmpty()) {
            return Optional.empty();
        }

        // Todo: perhaps better to supply format options:
        var format = properties.getProperty(DIRECTORY_BOT_FILEBOT_RENAME_FORMAT).get();

        try {
            var tv = runFilebot(filePath, targetDirectory, format, "TheTVDB");
            var movies = runFilebot(filePath, targetDirectory, format, "TheMovieDB");

            var lines = Stream.concat(tv.stream(), movies.stream()).toList();

            for (var output : lines) {
                var matcherFromTo = FROM_TO_PATTERN.matcher(output);
                var matcherSkippedBecause = SKIPPED_BECAUSE_PATTERN.matcher(output);

                if (matcherFromTo.find()) {
                    return Optional.of(matcherFromTo.group(2));
                } else if (matcherSkippedBecause.find()) {
                    return Optional.of(matcherSkippedBecause.group(2));
                }
            }

        } catch (Exception e) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    private static List<String> runFilebot(String fileAbsolutePath, String targetDirectory, String format, String database)  {
        try {
            var processBuilder = new ProcessBuilder("filebot", "-rename", fileAbsolutePath, "--action", "test", "--output", targetDirectory, "--format", format, "--db", database);
            Process process = null;
            process = processBuilder.start();
            var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            List<String> lines = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    lines.add(line);
                }
            }

            process.waitFor();
            return lines;
        } catch (IOException | InterruptedException e) {
            log.error("Executing filebot: {}", e.getMessage());
        }

        return List.of();
    }
}

