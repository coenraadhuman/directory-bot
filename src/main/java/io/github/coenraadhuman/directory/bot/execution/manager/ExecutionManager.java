package io.github.coenraadhuman.directory.bot.execution.manager;

import io.github.coenraadhuman.directory.bot.configuration.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

import static io.github.coenraadhuman.directory.bot.configuration.Property.DIRECTORY_BOT_SINGLE_EXECUTION;

public class ExecutionManager {

    private static final Logger log = LoggerFactory.getLogger(ExecutionManager.class);

    public static void execute(Properties properties, Runnable runnable) {
        properties.getProperty(DIRECTORY_BOT_SINGLE_EXECUTION)
                .map(Boolean::valueOf)
                .ifPresentOrElse(shouldSingleExecute -> {
                    if (shouldSingleExecute) {
                        singleExecution(runnable);
                    } else {
                        infiniteExecution(runnable);
                    }
                }, () ->{
                    log.error("Misconfiguration, single execution configuration not available defaulting to infinite execution");
                    infiniteExecution(runnable);
                });
    }

    private static void infiniteExecution(Runnable runnable) {
        var nextExecutionTime = Instant.now();
        while (true) {
            if (nextExecutionTime.isBefore(Instant.now())) {
                final var elapsed = singleExecution(runnable);

                nextExecutionTime = elapsed.toMillis() > 10000
                        ? Instant.now().plusMillis(elapsed.toMillis())
                        : Instant.now().plusSeconds(10);

                log.info("Next execution set to: {}", nextExecutionTime);
            }
        }
    }

    private static Duration singleExecution(Runnable runnable) {
        log.info("Executing sync");

        final var start = Instant.now();
        runnable.run();

        final var elapsed = Duration.between(start, Instant.now());
        log.info("Sync took: {} ms", elapsed.toMillis());

        return elapsed;
    }

}
