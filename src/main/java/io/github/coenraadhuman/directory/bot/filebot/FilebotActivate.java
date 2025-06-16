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

public class FilebotActivate {

    private static final Logger log = LoggerFactory.getLogger(FilebotActivate.class);

    public static void activate(Properties properties)  {
        try {
            var licenseDirectory = properties.getProperty(Property.DIRECTORY_BOT_CONFIGURATION_DIRECTORY).orElse("/config");
            var processBuilder = new ProcessBuilder( "filebot", "--license", licenseDirectory + "/license.psm");
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

            lines.forEach(output -> log.info("{}", output));
        } catch (IOException | InterruptedException e) {
            log.error("Executing filebot: {}", e.getMessage());
        }
    }

}
