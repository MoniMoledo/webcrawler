package edu.uci.ics.cloudberry.asterix;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.util.ArrayList;

/**
 * Created by monique on 09/10/17.
 *
 * This file contains code that was borrowed from https://github.com/ISG-ICS/cloudberry.
 *
 * Copyright: mixed. See noah/LICENSE for copyright and licensing information.
 *
 */

public class AsterixConfig {
    @Option(name = "-u", aliases = "--url", usage = "url of the feed adapter")
    private String adapterUrl;

    @Option(name = "-p", aliases = "--port", usage = "port of the feed socket")
    private int port;

    @Option(name = "-w", aliases = "--wait", usage = "waiting milliseconds per record, default 500")
    private int waitMillSecPerRecord = 500;

    @Option(name = "-b", aliases = "--batch", usage = "batchsize per waiting periods, default 50")
    private int batchSize = 50;

    @Option(name = "-c", aliases = "--count", usage = "maximum number to feed, default unlimited")
    private int maxCount = Integer.MAX_VALUE;

    @Option(name = "-fo", aliases = "--file-only", usage = "only store in a file, do not geotag nor ingest")
    private boolean isFileOnly = false;

    public String getAdapterUrl() {
        return adapterUrl;
    }

    public int getPort() {
        return port;
    }

    public int getWaitMillSecPerRecord() {
        return waitMillSecPerRecord;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public boolean isFileOnly() {
        return isFileOnly;
    }

    public static class TermArrayOptionHandler extends OptionHandler<String[]> {

        public TermArrayOptionHandler(CmdLineParser parser, OptionDef option,
                                      Setter<? super String[]> setter) {
            super(parser, option, setter);
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            int counter = 0;
            ArrayList<String> terms = new ArrayList<String>();
            while (true) {
                String param;
                try {
                    param = params.getParameter(counter);
                } catch (CmdLineException ex) {
                    ex.printStackTrace();
                    System.out.println("track term exception");
                    break;
                }
                if (param.startsWith("-")) {
                    break;
                }
                for (String str : param.split(",")) {
                    if (str.trim().length() > 0) {
                        terms.add(str.trim());
                    }
                }
                counter++;
            }
            Setter s = this.setter;
            for (String term : terms)
                s.addValue(term);
            return counter;

        }

        @Override
        public String getDefaultMetaVariable() {
            return "String[]";
        }
    }

}
