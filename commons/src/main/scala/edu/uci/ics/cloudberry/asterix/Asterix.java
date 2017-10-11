package edu.uci.ics.cloudberry.asterix;

/**
 * Created by monique on 09/10/17.
 */
public class Asterix {
    public static FeedSocketAdapterClient openSocket(AsterixConfig config) throws Exception {
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
}
