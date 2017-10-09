import integration.AsterixIntegration;
import asterix.FeedSocketAdapterClient;
import com.google.gson.*;
import integration.TextGeoLocatorIntegration;
import integration.webhose.WebhoseIntegration;
import util.CmdLineAux;
import util.Config;
import util.FileLogger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class Crawler {

    private static Logger logger = FileLogger.getLogger();
    private static TextGeoLocatorIntegration geoLocator = new TextGeoLocatorIntegration();

    public static BufferedWriter createWriter(String fileName) throws IOException {

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        fileName += strDate + ".gz";
        GZIPOutputStream zip = new GZIPOutputStream(
                new FileOutputStream(new File(fileName)));
        BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(zip, "UTF-8"));
        return bw;
    }

    private static long calculateTimestamp(int numberOfDays) {
        long millisecondsAgo = TimeUnit.DAYS.toMillis(numberOfDays);
        long currentMilliseconds = System.currentTimeMillis();

        return currentMilliseconds - millisecondsAgo;
    }

    public static void main(String[] args) throws Exception {

        Config config = CmdLineAux.parseCmdLine(args);

        FeedSocketAdapterClient feedSocket = AsterixIntegration.openSocket(config);

        WebhoseIntegration webhose = new WebhoseIntegration(config.getApiKey());

        try (BufferedWriter bw = createWriter("webhose")) {

            long timestamp = calculateTimestamp(config.getDays());

            int requestsLeft = 1000;

            while (requestsLeft > 0) {
                try {
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

                            String geoTagValue = geoLocator.geoTag(config.getTextGeoLocatorUrl(), post.getAsJsonObject().get("text").getAsString());
                            JsonElement jsonGeoTag = null;
                            if (geoTagValue != null) {
                                jsonGeoTag = new JsonParser().parse(geoTagValue);
                            }
                            post.getAsJsonObject().add("geo_tag", jsonGeoTag);
                            String adm = AsterixIntegration.convertToADM(post);
                            feedSocket.ingest(adm);
                            bw.write(post.toString());
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

}