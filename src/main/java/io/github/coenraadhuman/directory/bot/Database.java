package io.github.coenraadhuman.directory.bot;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static io.github.coenraadhuman.directory.bot.ApplicationProperties.DATABASE_CONNECTION;

public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private Database() {
        throw new RuntimeException("This is a utility class and should not be constructed");
    }

    public static void migrate(Properties properties) {
        var databaseConnection = properties.getProperty(DATABASE_CONNECTION);

        log.info("Determined database connection: {}", databaseConnection);
        Flyway.configure()
                .dataSource(databaseConnection, "", "") // Todo: should be property
                .load()
                .migrate();
    }

    public static Jdbi retrieveDatabaseConnection(Properties properties) {
        return Jdbi.create(properties.getProperty(DATABASE_CONNECTION), "", ""); // Todo: should be property
    }

}
