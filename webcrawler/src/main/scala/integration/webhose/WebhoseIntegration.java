package integration.webhose;

import com.google.gson.JsonElement;
import util.FileLogger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by monique on 24/09/17.
 */
public class WebhoseIntegration {

    private static Logger logger = FileLogger.getLogger();

    WebhoseIOClient webhoseClient;

    public WebhoseIntegration(String apiKey) {
        webhoseClient = WebhoseIOClient.getInstance(apiKey);
    }

    public JsonElement query(long timestamp, String[] keywords, String countryCode, boolean isNext) throws Exception {

        JsonElement result = null;

        try {
            if (isNext) {
                result = webhoseClient.getNext();
            } else {
                // Create set of queries
                Map<String, String> queries = new HashMap();
                String query = getQueryString(keywords, countryCode);

                queries.put("q", query);
                queries.put("ts", String.valueOf(timestamp));

                // Fetch query result
                result = this.webhoseClient.query("filterWebContent", queries);
            }
        } catch (IOException ex) {

            int httpResponseCode = getHttpResponseCode(ex.getMessage());

            switch (httpResponseCode) {
                case 400:
                    logger.log(Level.SEVERE, "Wrong sort or order value");
                    throw ex;
                case 429:
                    logger.log(Level.SEVERE, "Request or rate limit exceeded");
                    Thread.sleep(1000);
                    break;
                case 500:
                    logger.log(Level.SEVERE, "Failed to execute query: API internal error");
                    Thread.sleep(10000);
                    break;
                default:
                    Supplier<String> msg = () -> ex.getMessage();
                    logger.log(Level.SEVERE, ex, msg);
                    throw ex;
            }
        }
        return result;
    }

    public JsonElement queryNext() throws Exception {
        return query(0, new String[0], "", true);
    }

    private static int getHttpResponseCode(String exceptionMessage) {

        if (exceptionMessage.contains("HTTP response code: ")) {
            int length = "HTTP response code: ".length();
            int startIndex = exceptionMessage.indexOf("HTTP response code: ") + length;

            int httpResponseCode = Integer.parseInt(exceptionMessage.substring(startIndex, startIndex + 3));

            return httpResponseCode;
        } else {
            return -1;
        }
    }

    private String getQueryString(String[] keywords, String countryCode) {

        String OR = " OR ";
        String AND = " AND ";
        String location_filter = "location: ";
        String thread_country_filter = "thread.country: ";

        String optionalKeywords = String.join(OR, keywords);

        String query = "(" + optionalKeywords + ")" + AND + thread_country_filter + countryCode;

        return query;
    }
}
