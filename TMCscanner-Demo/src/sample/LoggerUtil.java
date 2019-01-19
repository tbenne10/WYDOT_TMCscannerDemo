package sample;

import java.io.IOException;
import java.util.logging.*;
import java.util.logging.Logger;



public class LoggerUtil {
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;

    static private FileHandler fileHTML;
    static private Formatter formatterHTML;

    //Max log file size (100 MB default)
    public static final int FILE_SIZE = 1024 * 1024 * 100;

    static public void setup() throws IOException {


        // get the global logger to configure it
        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        logger.setLevel(Level.INFO);
        fileTxt = new FileHandler(Controller.jarPath + "Logs/Logging.txt", FILE_SIZE , 1, true );
        fileHTML = new FileHandler(Controller.jarPath + "Logs/Logging.html", FILE_SIZE , 1, true);

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        logger.addHandler(fileTxt);

        // create an HTML formatter
        formatterHTML = new HtmlFormatter();
        fileHTML.setFormatter(formatterHTML);
        logger.addHandler(fileHTML);
    }
}