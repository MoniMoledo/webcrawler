package edu.uci.ics.cloudberry.noah.feed;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

/*
 * This file contains code that was borrowed from https://github.com/ISG-ICS/cloudberry.
 *
 * Copyright: mixed. See noah/LICENSE for copyright and licensing information.
 */

public class CmdLineAux {

    public static Config parseCmdLine(String[] args) {
        Config config = new Config();
        CmdLineParser parser = new CmdLineParser(config);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e);
            parser.printUsage(System.err);
        }return config;
    }

    public static ResponseList<User> getUsers(Config config, Twitter twitter) throws CmdLineException {

        ResponseList<User> users = null;
        try {
            users = twitter.lookupUsers(config.getTrackUsers());
        } catch (TwitterException ex) {
            throw new CmdLineException("No user was found, please check the username(s) provided");
        }
        return users;
    }

    public static Twitter getTwitterInstance(Config config) {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.setDebugEnabled(true)
                .setOAuthConsumerKey(config.getConsumerKey())
                .setOAuthConsumerSecret(config.getConsumerSecret())
                .setOAuthAccessToken(config.getToken())
                .setOAuthAccessTokenSecret(config.getTokenSecret())
                .setJSONStoreEnabled(true);

        TwitterFactory factory = new TwitterFactory(builder.build());
        Twitter twitter = factory.getInstance();
        return twitter;
    }
}