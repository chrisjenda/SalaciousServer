package SalaciousServer.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

/**
 * Wrapper class for printing SalaciousServer logs with Log4j 2 logger. To configure console logging
 * level launch SalaciousServer with {@code JVM_PROPERTY} set to a custom logger level and call
 * {@link #initialize()} method.
 *
 * <p>Logs will automatically be printed to console and configured log files. Check {@code
 * log4j2.xml} for log file locations. Use the {@code static} methods to print logs with desired log
 * level. If you want to print with a log level not covered by {@code static} methods use {@link
 * #get()} method to get a reference to logger instance.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class SalaciousServerLogger {

  public static final Level VERBOSE = Level.forName("VERBOSE", 450);
  static final String LOGGER_PROPERTY = "salaciousserver.logger";

  private static final Logger LOGGER = LogManager.getLogger("SalaciousServer");

  /* Make the constructor private to disable instantiation */
  private SalaciousServerLogger() {
    throw new UnsupportedOperationException();
  }

  /**
   * Initialize {@link SalaciousServerLogger} system by setting logging level resolved from system
   * properties. To configure console logging level launch SalaciousServer with {@code JVM_PROPERTY}
   * set to a custom logger level.
   */
  public static void initialize() {
    String sLevel = System.getProperty(LOGGER_PROPERTY);
    if (sLevel != null && !sLevel.isEmpty()) {
      Level level = Level.getLevel(sLevel);
      if (level != null) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        LoggerConfig rootLoggerConfig = config.getLoggers().get("");
        rootLoggerConfig.removeAppender("SalaciousServerConsole");
        rootLoggerConfig.addAppender(config.getAppender("SalaciousServerConsole"), level, null);

        ctx.updateLoggers();
        LOGGER.info("Setting custom level for SalaciousServer logger '" + sLevel + '\'');
      } else LOGGER.error("Unable to resolve logging level '" + sLevel + '\'');
    }
    LOGGER.info("Initialized SalaciousServer logger");
  }

  /** Returns an instance of Log4j {@link Logger} used for logging. */
  public static Logger get() {
    return LOGGER;
  }

  /** Logs a message object with the {@link Level#INFO INFO} level. */
  public static void info(String log) {
    LOGGER.info(log);
  }

  /**
   * Logs a formatted message with {@link Level#INFO INFO} level using the specified format string
   * and arguments.
   *
   * @param format the format {@code String}.
   * @param params arguments specified by the format.
   */
  public static void info(String format, Object... params) {
    LOGGER.printf(Level.INFO, format, params);
  }

  /** Logs a message object with the {@link SalaciousServerLogger#VERBOSE VERBOSE} level. */
  public static void detail(String log) {
    LOGGER.log(SalaciousServerLogger.VERBOSE, log);
  }

  /**
   * Logs a formatted message with {@link SalaciousServerLogger#VERBOSE VERBOSE} level using the
   * specified format string and arguments.
   *
   * @param format the format {@code String}.
   * @param params arguments specified by the format.
   */
  public static void detail(String format, Object... params) {
    LOGGER.printf(SalaciousServerLogger.VERBOSE, format, params);
  }

  /**
   * Logs a message object with the {@link Level#ERROR ERROR} level.
   *
   * @param message the message string to log.
   */
  public static void error(String message) {
    LOGGER.error(message);
  }

  /**
   * Logs a formatted message with {@link Level#ERROR ERROR} level using the specified format string
   * and arguments.
   *
   * @param format the format {@code String}.
   * @param params arguments specified by the format.
   */
  public static void error(String format, Object... params) {
    LOGGER.printf(Level.ERROR, format, params);
  }

  /**
   * Logs a message at the {@link Level#ERROR ERROR} level including the stack trace of the {@link
   * Throwable} <code>t</code> passed as parameter.
   *
   * @param message the message object to log.
   * @param t the exception to log, including its stack trace.
   */
  public static void error(String message, Throwable t) {
    LOGGER.error(message, t);
  }

  /**
   * Logs a message object with the {@link Level#WARN WARN} level.
   *
   * @param message the message string to log.
   */
  public static void warn(String message) {
    LOGGER.warn(message);
  }

  /**
   * Logs a formatted message with {@link Level#WARN WARN} level using the specified format string
   * and arguments.
   *
   * @param format the format {@code String}.
   * @param params arguments specified by the format.
   */
  public static void warn(String format, Object... params) {
    LOGGER.printf(Level.WARN, format, params);
  }

  /**
   * Logs a message object with the {@link Level#DEBUG DEBUG} level.
   *
   * @param message the message string to log.
   */
  public static void debug(String message) {
    LOGGER.debug(message);
  }

  /**
   * Logs a formatted message with {@link Level#DEBUG DEBUG} level using the specified format string
   * and arguments.
   *
   * @param format the format {@code String}.
   * @param params arguments specified by the format.
   */
  public static void debug(String format, Object... params) {
    LOGGER.printf(Level.DEBUG, format, params);
  }

  /**
   * Logs a message at the {@link Level#DEBUG DEBUG} level including the stack trace of the {@link
   * Throwable} <code>t</code> passed as parameter.
   *
   * @param message the message to log.
   * @param t the exception to log, including its stack trace.
   */
  public static void debug(String message, Throwable t) {
    LOGGER.debug(message, t);
  }

  /**
   * Logs a formatted message using the specified format string and arguments.
   *
   * @param level The logging Level.
   * @param format The format String.
   * @param params Arguments specified by the format.
   */
  public static void printf(Level level, String format, Object... params) {
    LOGGER.printf(level, format, params);
  }

  /**
   * This handler will log an error with Log4J when a Thread abruptly terminates due to an uncaught
   * exception. We need to log unhandled exceptions with Log4J otherwise they will not appear in the
   * log file.
   */
  public static class Log4JUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * Handle uncaught exception thrown by SalaciousServer by printing an error with Log4J. The
     * error will include the exception stack trace and will be included in the log file.
     *
     * @param t {@code Thread} that is throwing the exception.
     * @param e exception being thrown by thread.
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      SalaciousServerLogger.error("Uncaught exception was thrown", e);
    }
  }
}
