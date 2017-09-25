package integration;

import asterix.FeedSocketAdapterClient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import util.Config;

import java.util.UUID;

/**
 * Created by monique on 24/09/17.
 */
public class AsterixIntegration {

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

    public static String insertDashUUID(String uuid) {
        StringBuffer sb = new StringBuffer(uuid);
        sb.setCharAt(8, '-');

        sb = new StringBuffer(sb.toString());
        sb.setCharAt(13, '-');

        sb = new StringBuffer(sb.toString());
        sb.setCharAt(18, '-');

        sb = new StringBuffer(sb.toString());
        sb.setCharAt(23, '-');

        return sb.substring(0, 36).toString();
    }

    private static String convertToADMDatetime(String datetime) {
        return "datetime(\"" + datetime + "\")";
    }

    public static String convertToADM(JsonElement post) {

        JsonObject postWithId = post.getAsJsonObject();

        String crawled = post.getAsJsonObject().get("crawled").getAsString();
        String published = post.getAsJsonObject().get("published").getAsString();
        String threadPublished = post.getAsJsonObject().get("thread").getAsJsonObject().get("published").getAsString();
        String uuid = post.getAsJsonObject().get("uuid").getAsString();

        String admCrawled = convertToADMDatetime(crawled);
        String admPublished = convertToADMDatetime(published);
        String admThreadPublished = convertToADMDatetime(threadPublished);
        UUID uid = UUID.fromString(insertDashUUID(uuid));
        String admUuid = "uuid(\"" + uid + "\")";

        postWithId.addProperty("crawled", admCrawled);
        postWithId.addProperty("published", admPublished);
        JsonObject admThread = post.getAsJsonObject().get("thread").getAsJsonObject();
        admThread.addProperty("published", admThreadPublished);
        admThread.addProperty("uuid", admUuid);
        postWithId.add("thread", admThread);
        postWithId.addProperty("uuid", admUuid);

        String postString = postWithId.toString();
        String admPost = postString.replaceAll("\\\"datetime\\((.{33})\\)\\\"", "datetime($1)");
        String admPost2 = admPost.replaceAll("\\\"uuid\\((.{40})\\)\\\"", "uuid($1)");
        String adm3 = admPost2.replaceAll("\\\\", "");
        return adm3;
    }
}
