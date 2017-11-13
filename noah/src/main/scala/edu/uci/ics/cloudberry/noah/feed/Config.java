package edu.uci.ics.cloudberry.noah.feed;

import com.twitter.hbc.core.endpoint.Location;

import edu.uci.ics.cloudberry.asterix.AsterixConfig;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OptionHandler;
import org.kohsuke.args4j.spi.Parameters;
import org.kohsuke.args4j.spi.Setter;

import java.util.ArrayList;
/*
 * This file contains code that was borrowed from https://github.com/ISG-ICS/cloudberry.
 *
 * Copyright: mixed. See noah/LICENSE for copyright and licensing information.
 */

public class Config extends AsterixConfig {
    @Option(name = "-ck", aliases = "--consumer-key", usage = "ConsumerKey for Twitter OAuth")
    private String consumerKey = null;

    @Option(name = "-cs", aliases = "--consumer-secret", usage = "Consumer Secret for Twitter OAuth")
    private String consumerSecret = null;

    @Option(name = "-tk", aliases = "--token", usage = "Token for Twitter OAuth")
    private String token = null;

    @Option(name = "-ts", aliases = "--token-secret", usage = "Token secret for Twitter OAuth")
    private String tokenSecret = null;

    @Option(name = "-tr", aliases = "--tracker", handler = TermArrayOptionHandler.class, usage = "Tracked terms, separated by comma.")
    private String[] trackTerms = new String[]{};

    @Option(name = "-tu", aliases = "--track-user", handler = TermArrayOptionHandler.class, usage = "Tracked public users, by username, separated by comma.")
    private String[] trackUsers = new String[]{};

    @Option(name = "-loc", aliases = "--location", handler =  LocationListOptionHandler.class, usage = "location rectangular, southwest.lon, southwest.lat, northeast.lon, northeast.lat")
    private Location[] trackLocation = new Location[]{};

    @Option(name = "-axs", aliases = "--asterix-server", usage = "server:port for AsterixDB requests")
    private String axServer = "http://kiwi.ics.uci.edu:19002";

    @Option(name = "-dv", aliases = "--dataverse-zika-twitter", usage = "Dataverse name for zika related tweets")
    private String dataverse = "twitter";

    @Option(name = "-uds", aliases = "--users-dataset", usage = "Dataset name for zika related tweets from specific users ")
    private String usersDataset = "ds_users_tweet";

    @Option(name = "-zds", aliases = "--zika-dataset", usage = "Dataset name for streaming zika related tweets")
    private String zikaStreamDataset = "ds_zika_streaming";

    @Option(name = "-fp", aliases = "--file-path", usage = "GZIP file path")
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public String getToken() {
        return token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public Location[] getTrackLocation() {
        return trackLocation;
    }

    public String[] getTrackTerms() {
        return trackTerms;
    }

    public String[] getTrackUsers() {
        return trackUsers;
    }


    public enum Source {
        Zika, User, HistUser
    }

    public String getDataset(Source source){
        switch (source){
            case Zika: return zikaStreamDataset;
            case User:
            case HistUser: return usersDataset;
            default: return null;
        }
    }

    public String getAxServer() { return axServer; }

    public String getDataverse() { return dataverse; }

    public static class LocationListOptionHandler extends OptionHandler<Location[]> {

        public LocationListOptionHandler(CmdLineParser parser, OptionDef option,
                                         Setter<? super Location[]> setter) {
            super(parser, option, setter);
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            int counter = 0;
            ArrayList<Location> locations = new ArrayList<Location>();
            while (true) {
                String param;
                try {
                    param = params.getParameter(counter);
                } catch (CmdLineException ex) {
                    break;
                }
                if (param.startsWith("-") && param.length() > 1 && !Character.isDigit(param.charAt(1))) {
                    break;
                }

                String[] points = param.split(",");
                if (points.length % 4 != 0) {
                    throw new CmdLineException("The number of point for one rectangular should be four");
                }
                for (int i = 0; i < points.length; i += 4) {
                    locations.add(new Location(
                            new Location.Coordinate(Double.parseDouble(points[i].trim()),
                                    Double.parseDouble(points[i + 1].trim())),
                            new Location.Coordinate(Double.parseDouble(points[i + 2].trim()),
                                    Double.parseDouble(points[i + 3].trim()))));
                }
                counter++;
            }//while true

            Setter s = this.setter;
            for (Location loc : locations) {
                s.addValue(loc);
            }
            return counter;
        }

        @Override
        public String getDefaultMetaVariable() {
            return "Location";
        }
    }

}
