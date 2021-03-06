package edu.uci.ics.cloudberry.noah.feed;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import edu.uci.ics.cloudberry.asterix.Asterix;
import edu.uci.ics.cloudberry.asterix.FeedSocketAdapterClient;
import edu.uci.ics.cloudberry.noah.adm.UnknownPlaceException;
import edu.uci.ics.cloudberry.util.FileHelper;
import org.kohsuke.args4j.CmdLineException;
import twitter4j.TwitterException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * This file contains code that is based on https://github.com/ISG-ICS/cloudberry.
 *
 * Copyright: mixed. See noah/LICENSE for copyright and licensing information.
 */

public class TwitterFeedStreamDriver {

    Client twitterClient;
    volatile boolean isConnected = false;

    public void run(Config config, BufferedWriter bw, FeedSocketAdapterClient socketAdapterClient)
            throws InterruptedException, IOException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        // add some track terms

        if (config.getTrackTerms().length != 0) {
            System.err.print("set track terms are: ");
            for (String term : config.getTrackTerms()) {
                System.err.print(term);
                System.err.print(" ");
            }
            System.err.println();
            endpoint.trackTerms(Lists.newArrayList(config.getTrackTerms()));
        }
        if (config.getTrackLocation().length != 0) {

            System.err.print("set track locations are:");
            for (Location location : config.getTrackLocation()) {
                System.err.print(location);
                System.err.print(" ");
            }
            System.err.println();

            endpoint.locations(Lists.<Location>newArrayList(config.getTrackLocation()));
        }

        Authentication auth = new OAuth1(config.getConsumerKey(), config.getConsumerSecret(), config.getToken(),
                config.getTokenSecret());

        // Create a new BasicClient. By default gzip is enabled.
        twitterClient = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        // Establish a connection
        try {
            twitterClient.connect();
            isConnected = true;

            // Do whatever needs to be done with messages;
            while (!twitterClient.isDone()) {
                String msg = queue.take();

                //if is not to store in file only, geo tag and send to database
                if (!config.isFileOnly()) {
                    try {
                        String adm = TagBrTweet.tagOneTweet(msg, true);
                        bw.write(adm);
                        socketAdapterClient.ingest(adm);
                    } catch (UnknownPlaceException e) {

                    } catch (TwitterException e) {
                        e.printStackTrace(System.err);
                    }
                }else{
                    bw.write(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            if (bw != null)
                bw.close();
            if (twitterClient != null)
                twitterClient.stop();
        }
    }

    public static void main(String[] args) throws IOException {
        TwitterFeedStreamDriver feedDriver = new TwitterFeedStreamDriver();
        FeedSocketAdapterClient socketAdapterClient = null;
        try {
            Config config = CmdLineAux.parseCmdLine(args);
            BufferedWriter bw = FileHelper.createWriter("Tweet_");

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    if (feedDriver.twitterClient != null && feedDriver.isConnected) {
                        feedDriver.twitterClient.stop();
                    }
                }
            });

            if (config.getTrackTerms().length == 0 && config.getTrackLocation().length == 0) {
                throw new CmdLineException("Should provide at least one tracking word, or one location boundary");
            }
            if(!config.isFileOnly()){
                socketAdapterClient = Asterix.openSocket(config);
            }
            feedDriver.run(config, bw, socketAdapterClient);
        } catch (CmdLineException e) {
            e.printStackTrace(System.err);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            if (socketAdapterClient != null) {
                socketAdapterClient.finalize();
            }
        }
    }

}
