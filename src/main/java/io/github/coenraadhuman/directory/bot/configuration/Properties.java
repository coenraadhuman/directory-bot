package io.github.coenraadhuman.directory.bot.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Properties extends java.util.Properties {

    private static final Logger log = LoggerFactory.getLogger(Properties.class);

    public Optional<String> getProperty(Property key) {
        return Optional.ofNullable(super.getProperty(key.toString()));
    }

    public synchronized Object put(Property key, Object value) {
        return super.put(key.toString(), value);
    }

    public synchronized Object putIfAbsent(Property key, Object value) {
        return super.putIfAbsent(key.toString(), value);
    }

    public synchronized void loadEnvironmentVariables() {
        for (var property : Property.values()) {
            EnvironmentVariables.getVariable(property)
                    .ifPresentOrElse(
                  propertyValueAsEnv -> {
                      log.info("Property: {} using environment value: {}", property, propertyValueAsEnv);
                      this.putIfAbsent(property, propertyValueAsEnv);
                    }, () ->
                      property.defaultValue().ifPresent(defaultValue -> {
                          log.info("Property: {} using default value: {}", property, defaultValue);
                          this.putIfAbsent(property, defaultValue);
                    }));
        }
    }

}
