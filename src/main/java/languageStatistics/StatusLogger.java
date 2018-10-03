package languageStatistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class StatusLogger {
    private static final Logger LOG = LoggerFactory.getLogger("Progress logger");

    public static void logInfo(String message) {
        LOG.info(message);
    }

    public static void gap() {
        System.out.println();
    }

    public static void logCollecting(String what) {
        System.out.println();
        MDC.put("collecting", what.toUpperCase());
        LOG.info("COLLECTING");
        MDC.clear();
    }

    public static void logSuccessFor(String language) {
        MDC.put("status", "SUCCESS");
        LOG.info(language);
        MDC.clear();
    }

    public static void logSkipped(String language) {
        MDC.put("status", "SKIPPED");
        LOG.info(language);
        MDC.clear();
    }

    public static void logSkipped(String language, String cause) {
        MDC.put("status", "SKIPPED");
        MDC.put("cause", cause);
        LOG.info(language);
        MDC.clear();
    }

    public static void logChecking(String language) {
        System.out.println();
        MDC.put("collecting", language.toUpperCase());
        LOG.info("CHECKING");
        MDC.clear();
    }

    public static void logSuccess(String data) {
        MDC.put("status", "SUCCESS");
        LOG.info(data);
        MDC.clear();
    }

    public static void logError(String data) {
        MDC.put("error", " FAILURE");
        LOG.info(data);
        MDC.clear();
//        System.exit(1);
    }

    public static void logErrorFor(String data, String cause) {
        MDC.put("error", " FAILURE");
        MDC.put("cause", cause);
        LOG.info(data);
        MDC.clear();
//        System.exit(1);
    }

    public static void logError() {
        LOG.error("Something went wrong.");
//        System.exit(1);
    }

    public static void logException(String message, Exception e) {
        LOG.error(message, e);
//        System.exit(1);
    }

    public static void appendWarning(String cause) {
        if (MDC.get("cause") != null) {
            cause = MDC.get("cause") + " | " + cause;
        }
        MDC.put("cause", cause);

    }
}
