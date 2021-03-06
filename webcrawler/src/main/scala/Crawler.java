import edu.uci.ics.cloudberry.asterix.Asterix;
import edu.uci.ics.cloudberry.asterix.FeedSocketAdapterClient;
import edu.uci.ics.cloudberry.util.FileHelper;
import integration.AsterixIntegration;
import com.google.gson.*;
import integration.TextGeoLocatorIntegration;
import integration.webhose.WebhoseIntegration;
import util.CmdLineAux;
import util.Config;
import util.FileLogger;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Crawler {

    private static Logger logger = FileLogger.getLogger();

    private static TextGeoLocatorIntegration geoLocator = new TextGeoLocatorIntegration();

    public static void main(String[] args) throws Exception {

        Config config = CmdLineAux.parseCmdLine(args);

        FeedSocketAdapterClient feedSocket = null;

        WebhoseIntegration webhose = new WebhoseIntegration(config.getApiKey());

        long timestamp = CmdLineAux.calculateTimestamp(config.getDays());

        int requestsLeft = 1000;

        if(!config.isFileOnly()){
            feedSocket = Asterix.openSocket(config);
        }

        while (requestsLeft > 0) {
            try (BufferedWriter bw = FileHelper.createWriter("webhose")) {
                // Fetch query result
                JsonElement result = webhose.query(timestamp, config.getKeywords(), config.getCountry(), false);
                int moreResultsAvailable;

                requestsLeft = result.getAsJsonObject().get("requestsLeft").getAsInt();
                int totalResults = result.getAsJsonObject().get("totalResults").getAsInt();

                logger.info("Requests left: " + requestsLeft + " Total results: " + totalResults);

                do {
                    moreResultsAvailable = result.getAsJsonObject().get("moreResultsAvailable").getAsInt();
                    JsonArray results = result.getAsJsonObject().get("posts").getAsJsonArray();
                    for (JsonElement post : results) {

                        if(config.isFileOnly()){
                           bw.write(post.toString());
                        }else{
                            String geoTagValue = geoLocator.geoTag(config.getTextGeoLocatorUrl(), post.getAsJsonObject().get("text").getAsString());
                            JsonElement jsonGeoTag = null;
                            if (geoTagValue != null) {
                                jsonGeoTag = new JsonParser().parse(geoTagValue);

                                post.getAsJsonObject().add("geo_tag", jsonGeoTag);
                                String adm = AsterixIntegration.convertToADM(post);
                                feedSocket.ingest(adm);
                                bw.write(adm);
                                bw.write(",");
                            }

                        }
                    }
                    if (moreResultsAvailable > 0)
                        result = webhose.queryNext();
                } while (moreResultsAvailable > 0);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                throw ex;
            }
            Thread.sleep(86400000);
        }
    }

}