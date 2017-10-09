package util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created by monique on 05/06/17.
 */
public class FileLogger {

    public static Logger getLogger(){

        Logger logger = Logger.getLogger("WebCrawler");
        FileHandler fh;

        try {

            // This block configure the logger with handler and formatter
            fh = new FileHandler("WebCrawler.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logger;
    }
}
