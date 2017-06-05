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

    public String getApiKey() {
        return apiKey;
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
