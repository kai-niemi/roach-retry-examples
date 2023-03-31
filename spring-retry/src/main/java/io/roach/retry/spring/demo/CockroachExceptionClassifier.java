package io.roach.retry.spring.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;

import java.sql.SQLException;

public class CockroachExceptionClassifier {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String SERIALIZATION_FAILURE = "40001";

    public boolean shouldRetry(Throwable ex) {
        if (ex == null) {
            return false;
        }
        Throwable throwable = NestedExceptionUtils.getMostSpecificCause(ex);
        if (throwable instanceof SQLException) {
            return shouldRetry((SQLException) throwable);
        }
        logger.warn("Non-transient exception {}", ex.getClass());
        return false;
    }

    public boolean shouldRetry(SQLException ex) {
        if (SERIALIZATION_FAILURE.equals(ex.getSQLState())) {
            logger.warn("Transient SQL exception detected : sql state [{}], message [{}]",
                    ex.getSQLState(), ex.toString());
            return true;
        }
        return false;
    }
}
