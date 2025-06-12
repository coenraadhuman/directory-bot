package io.github.coenraadhuman.directory.bot.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Properties extends java.util.Properties {

    public Optional<String> getProperty(Property key) {
        return Optional.ofNullable(super.getProperty(key.toString()));
    }

    public synchronized Object put(Property key, Object value) {
        return super.put(key.toString(), value);
    }

    public synchronized Object putIfAbsent(Property key, Object value) {
        return super.putIfAbsent(key.toString(), value);
    }

    /**
     * In addition to loading properties file and its contents, it will load environment variable's value for corresponding property if provided.
     */
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        super.load(inStream);

        for (var property : Property.values()) {
            Optional.ofNullable(System.getenv(property.name()))
                    .ifPresent(propertyValueAsEnv -> this.putIfAbsent(property, propertyValueAsEnv));
        }
    }

}
