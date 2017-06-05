import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.webhoseio.sdk.WebhoseIOClient;
import util.CmdLineAux;
import util.Config;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

public class Crawler {

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

    private static String getQueryString(String keyword, String countryCode) {

        String OR = " OR ";
        String AND = " AND ";
        String location_filter = "location: ";
        String thread_country_filter = "thread.country: ";

//        String zika = "\"zika\"";
//        String febreAmarela = "\"febre amarela\"";
//        String chikungunya = "\"chikungunya\"";
//        String dengue = "\"dengue\"";
         String escapedKeyword = "\"" + keyword + "\"";

        String filters = "(" + escapedKeyword + ")" + AND + thread_country_filter + countryCode;

        return filters;
    }

    private static long calculateTimestamp(int numberOfDays) {
        long millisecondsAgo = TimeUnit.DAYS.toMillis(numberOfDays);
        long currentMilliseconds = System.currentTimeMillis();

        return currentMilliseconds - millisecondsAgo;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        Config config = CmdLineAux.parseCmdLineArgs(args);

        try (BufferedWriter bw = createWriter("webhose")) {

            String filters = getQueryString(config.getKeyword(), config.getCountry());
            long timestamp = calculateTimestamp(config.getDays());

            WebhoseIOClient webhoseClient = WebhoseIOClient.getInstance(config.getApiKey());
            // Create set of queries
            Map<String, String> queries = new HashMap<String, String>();

            queries.put("q", filters);
            queries.put("ts", String.valueOf(timestamp));

            // Fetch query result
            JsonElement result = webhoseClient.query("filterWebContent", queries);

            int moreResultsAvailable = result.getAsJsonObject().get("moreResultsAvailable").getAsInt();

            while (moreResultsAvailable > 0) {

                JsonArray results = result.getAsJsonObject().get("posts").getAsJsonArray();
                for (JsonElement post : results) {
                    bw.write(post.toString());
                }
                result = webhoseClient.getNext();
                moreResultsAvailable = result.getAsJsonObject().get("moreResultsAvailable").getAsInt();

            }
        }
    }
}