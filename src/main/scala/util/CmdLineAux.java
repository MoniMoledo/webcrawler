package util;


import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Created by monique on 04/06/17.
 */
public class CmdLineAux {

    public static Config parseCmdLineArgs(String[] args){

        Config config = new Config();
        CmdLineParser parser = new CmdLineParser(config);
        try{
            parser.parseArgument(args);
        }catch(CmdLineException ex){
            System.err.println(ex.toString());
        }
        return config;
    }
}
