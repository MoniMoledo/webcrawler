package util;


import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.util.ArrayList;

/**
 * Created by monique on 04/06/17.
 */
public class Config {

    @Option(name = "-tk", aliases = "--api-key", usage = "API key to use Webhose.io")
    private String apiKey = null;

    @Option(name = "-kw", aliases = "--keywords", handler = TermArrayOptionHandler.class, usage = "Keywords to search for")
    private String[] keywords = new String[]{};

    @Option(name = "-co", aliases = "--country-code", usage = "Thread country code")
    private String country = null;

    @Option(name = "-ds", aliases = "--days-ago", usage = "Crawl since the given number of days ago")
    private int days = 0;

    @Option(name = "-st", aliases = "--site", usage = "Search from specific site")
    private String site = null;

    @Option(name = "-u", aliases = "--url", usage = "Url of the feed adapter")
    private String adapterUrl;

    @Option(name = "-p", aliases = "--port", usage = "Port of the feed socket")
    private int port;

    @Option(name = "-w", aliases = "--wait", usage = "Waiting milliseconds per record, default 500")
    private int waitMillSecPerRecord = 500;

    @Option(name = "-b", aliases = "--batch", usage = "Batch size per waiting periods, default 50")
    private int batchSize = 50;

    @Option(name = "-c", aliases = "--count", usage = "Maximum number to feed, default unlimited")
    private int maxCount = Integer.MAX_VALUE;

    @Option(name = "-fo", aliases = "--file-only", usage = "Only store in a file, do not geotag nor ingest, default false")
    private boolean isFileOnly = false;

    @Option(name = "-tglurl", aliases = "--textgeolocatorurl", usage = "Url of the TextGeoLocator API")
    private String textGeoLocatorUrl = "http://localhost:9000/location";

    public String getApiKey() {
        return apiKey;
    }

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

    public String[] getKeywords() {
        return keywords;
    }

    public String getCountry() {
        return country;
    }

    public int getDays() {
        return days;
    }

    public String getSite() {
        return site;
    }

    public String getTextGeoLocatorUrl() {
        return textGeoLocatorUrl;
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
                    System.out.println("keywords exception");
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
