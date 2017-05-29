import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.webhoseio.sdk.WebhoseIOClient;

import java.io.*;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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

    private static String getQueryString() {

        String OR = " OR ";
        String AND = " AND ";
        String location_filter = "location: ";

        String zika = "\"zika\"";
        String febreAmarela = "\"febre amarela\"";
        String chikungunya = "\"chikungunya\"";
        String dengue = "\"dengue\"";

        String filters = "(" + zika + OR + febreAmarela + OR + dengue + OR + chikungunya + ")" + AND + location_filter + "brazil";

        return filters;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {

        try (BufferedWriter bw = createWriter("webhose")) {

            String api_key = "";
            String filters = getQueryString();

            WebhoseIOClient webhoseClient = WebhoseIOClient.getInstance(api_key);
            // Create set of queries
            Map<String, String> queries = new HashMap<String, String>();

            queries.put("q", filters);

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