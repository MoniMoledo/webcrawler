import asterix.FeedSocketAdapterClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.webhoseio.sdk.WebhoseIOClient;
import util.CmdLineAux;
import util.Config;
import util.FileLogger;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

public class Crawler {

    private static Logger logger = FileLogger.getLogger();

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

    public static FeedSocketAdapterClient openSocket(Config config) throws Exception {
        FeedSocketAdapterClient socketAdapterClient = null;
        if (config.getPort() != 0 && config.getAdapterUrl() != null) {
            if (!config.isFileOnly()) {
                String adapterUrl = config.getAdapterUrl();
                int port = config.getPort();
                int batchSize = config.getBatchSize();
                int waitMillSecPerRecord = config.getWaitMillSecPerRecord();
                int maxCount = config.getMaxCount();

                socketAdapterClient = new FeedSocketAdapterClient(adapterUrl, port,
                        batchSize, waitMillSecPerRecord, maxCount);
                socketAdapterClient.initialize();
            }
        } else {
            throw new Exception("You should provide a port and an URL");
        }
        return socketAdapterClient;
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

    private static String ConvertToADMDatetime(String datetime){
         return  "datetime(\"" + datetime + "\")";
    }

    private static String convertToADM(JsonElement post){

        long id = UUID.randomUUID().getMostSignificantBits();
        System.out.println(id);
        JsonObject postWithId = post.getAsJsonObject();
        postWithId.addProperty("id", id);

        String crawled = post.getAsJsonObject().get("crawled").getAsString();
        String published = post.getAsJsonObject().get("published").getAsString();
        String threadPublished = post.getAsJsonObject().get("thread").getAsJsonObject().get("published").getAsString();

        String admCrawled = ConvertToADMDatetime(crawled);
        String admPublished = ConvertToADMDatetime(published);
        String admThreadPublished = ConvertToADMDatetime(threadPublished);

        postWithId.addProperty("crawled", admCrawled);
        postWithId.addProperty("published", admPublished);
        JsonObject admThread = post.getAsJsonObject().get("thread").getAsJsonObject();
        admThread.addProperty("published", admThreadPublished);
        postWithId.add("thread", admThread);

        return postWithId.toString();
    }

    private static long calculateTimestamp(int numberOfDays) {
        long millisecondsAgo = TimeUnit.DAYS.toMillis(numberOfDays);
        long currentMilliseconds = System.currentTimeMillis();

        return currentMilliseconds - millisecondsAgo;
    }

    private static int getHttpResponseCode(String exceptionMessage) {

        if(exceptionMessage.contains("HTTP response code: ")) {
            int length = "HTTP response code: ".length();
            int startIndex = exceptionMessage.indexOf("HTTP response code: ") + length;

            int httpResponseCode = Integer.parseInt(exceptionMessage.substring(startIndex, startIndex + 3));

            return httpResponseCode;
        }else{
            return -1;
        }
    }

    public static void main(String[] args) throws Exception {

        Config config = CmdLineAux.parseCmdLineArgs(args);

        try (BufferedWriter bw = createWriter("webhose")) {

            String filters = getQueryString(config.getKeyword(), config.getCountry());
            long timestamp = calculateTimestamp(config.getDays());

            logger.info("Query string: " + filters);

            try {
                //FeedSocketAdapterClient feedSocket = Crawler.openSocket(config);
                WebhoseIOClient webhoseClient = WebhoseIOClient.getInstance(config.getApiKey());
                // Create set of queries
                Map<String, String> queries = new HashMap<String, String>();

                queries.put("q", filters);
                queries.put("ts", String.valueOf(timestamp));

                // Fetch query result
                JsonElement result = webhoseClient.query("filterWebContent", queries);

                int moreResultsAvailable = result.getAsJsonObject().get("moreResultsAvailable").getAsInt();
                int requestsLeft = result.getAsJsonObject().get("requestsLeft").getAsInt();
                int totalResults = result.getAsJsonObject().get("totalResults").getAsInt();

                logger.info("Requests left: " + requestsLeft + " Total results: " + totalResults);

                while (moreResultsAvailable > 0) {

                    JsonArray results = result.getAsJsonObject().get("posts").getAsJsonArray();
                    for (JsonElement post : results) {
                        String adm = convertToADM(post);
                        //feedSocket.ingest(adm);
                        bw.write(post.toString());
                    }
                    result = webhoseClient.getNext();
                    moreResultsAvailable = result.getAsJsonObject().get("moreResultsAvailable").getAsInt();

                }
            } catch (IOException ex) {

                int httpResponseCode = getHttpResponseCode(ex.getMessage());

                switch (httpResponseCode) {
                    case 400:
                        logger.log(Level.SEVERE, "Wrong sort or order value");
                        break;
                    case 429:
                        logger.log(Level.SEVERE, "Request or rate limit exceeded");
                        break;
                    case 500:
                        logger.log(Level.SEVERE,"Failed to execute query: API internal error - > sleep and try again");
                        break;
                    default:
                        Supplier<String> msg  = ()-> ex.getMessage();
                        logger.log(Level.SEVERE, ex, msg);
                        throw ex;
                }
            }
        }
    }
}