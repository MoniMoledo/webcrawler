package util;


import edu.uci.ics.cloudberry.asterix.AsterixConfig;
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
public class Config extends AsterixConfig {

    @Option(name = "-tk", aliases = "--api-key", usage = "API key to use Webhose.io")
    private String apiKey = null;

    @Option(name = "-kw", aliases = "--keywords", handler = TermArrayOptionHandler.class, usage = "Keywords to search for")
    private String[] keywords = new String[]{};

    @Option(name = "-co", aliases = "--country-code", usage = "Thread country code")
    private String country = "BR";

    @Option(name = "-ds", aliases = "--days-ago", usage = "Crawl since the given number of days ago")
    private int days = 1;

    @Option(name = "-st", aliases = "--site", usage = "Search from specific site")
    private String site = null;

    @Option(name = "-tglurl", aliases = "--textgeolocatorurl", usage = "Url of the TextGeoLocator API")

    private String textGeoLocatorUrl = "http://localhost:9000/location";

    public String getApiKey() {
        return apiKey;
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
}
