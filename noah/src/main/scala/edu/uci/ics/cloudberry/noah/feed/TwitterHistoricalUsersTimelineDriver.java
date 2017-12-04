package edu.uci.ics.cloudberry.noah.feed;

import edu.uci.ics.cloudberry.asterix.Asterix;
import edu.uci.ics.cloudberry.asterix.FeedSocketAdapterClient;
import edu.uci.ics.cloudberry.noah.adm.UnknownPlaceException;
import edu.uci.ics.cloudberry.util.FileHelper;
import org.kohsuke.args4j.CmdLineException;
import twitter4j.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

public class TwitterHistoricalUsersTimelineDriver {

    public void run(Config config,FeedSocketAdapterClient socketAdapterClient) throws IOException, CmdLineException {

        //Get historical user data
        try {
            Twitter twitter = CmdLineAux.getTwitterInstance(config);
            ResponseList<User> users = CmdLineAux.getUsers(config, twitter);
            for (User user : users) {
                BufferedWriter bw = FileHelper.createWriter("Tweet_User_"+user.getName() + "_");
                try {
                    if (user.getStatus() != null) {
                        //Paging in order to get all the tweets in the user timeline. Default is only the last 20.
                        int pageNum = 0;
                        while (user.getStatusesCount() > pageNum * 100) {
                            pageNum++;
                            Paging page = new Paging(pageNum,100);
                            List<Status> statuses = twitter.getUserTimeline(user.getId(), page);
                            for (Status status : statuses) {
                                try {
                                    String statusJson = TwitterObjectFactory.getRawJSON(status);
                                    String adm = null;
                                    adm = TagBrTweet.tagOneTweet(statusJson, true);

                                    bw.write(adm);
                                    socketAdapterClient.ingest(adm);
                                } catch (UnknownPlaceException e) {
                                    //e.printStackTrace();
                                }
                                catch (TwitterException e) {
                                e.printStackTrace(System.err);
                            }}
                        }
                    }
                } finally {
                    bw.close();
                }
            }
        } catch (TwitterException te) {
            System.err.println("User not found");
            te.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, CmdLineException {

        FeedSocketAdapterClient socketAdapterClient = null;
        TwitterHistoricalUsersTimelineDriver userDriver = new TwitterHistoricalUsersTimelineDriver();
        Config config = CmdLineAux.parseCmdLine(args);

        try {
            if (config.getTrackUsers().length == 0) {
                throw new CmdLineException("Should provide at least one tracking user");
            }
            socketAdapterClient = Asterix.openSocket(config);
            userDriver.run(config, socketAdapterClient);

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