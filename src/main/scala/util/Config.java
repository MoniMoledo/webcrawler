package util;


import org.kohsuke.args4j.Option;

/**
 * Created by monique on 04/06/17.
 */
public class Config {

    @Option(name = "-tk", aliases = "--api-key", usage = "API key to use Webhose.io")
    private String apiKey = null;

    @Option(name = "-kw", aliases = "--keyword", usage = "Keyword to search for")
    private String keyword = null;

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

    public String getKeyword() {
        return keyword;
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
}
