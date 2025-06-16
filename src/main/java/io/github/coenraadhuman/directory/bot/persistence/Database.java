package io.github.coenraadhuman.directory.bot.persistence;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

    private static final Logger log = LoggerFactory.getLogger(Database.class);

    private final String databaseConnection;

    // Todo: look into sqlite and make these properties for the user:
    private final String user = "";
    private final String password = "";

    private Jdbi jdbi;

    public Database(String databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void migrate() {
        Flyway.configure()
                .dataSource(databaseConnection, user, password)
                .load()
                .migrate();
        log.info("Database migration was completed.");
    }

    public Jdbi connection() {
        if (jdbi == null) {
            jdbi = Jdbi.create(databaseConnection, user, password);
            jdbi.installPlugin(new SqlObjectPlugin());
        }
        return jdbi;
    }

    public static String createDatabaseConnection(final String databaseDirectory) {
        return "jdbc:sqlite:%s/directory-bot.db".formatted(databaseDirectory);
    }

}
