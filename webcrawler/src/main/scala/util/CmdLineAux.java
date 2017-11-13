package util;


import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * Created by monique on 04/06/17.
 */
public class CmdLineAux {

    public static Config parseCmdLine(String[] args) {
        Config config = new Config();
        CmdLineParser parser = new CmdLineParser(config);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e);
            parser.printUsage(System.err);
        }
        return config;
    }

    public static long calculateTimestamp(int numberOfDays) {
        long millisecondsAgo = TimeUnit.DAYS.toMillis(numberOfDays);
        long currentMilliseconds = System.currentTimeMillis();

        return currentMilliseconds - millisecondsAgo;
    }
}
