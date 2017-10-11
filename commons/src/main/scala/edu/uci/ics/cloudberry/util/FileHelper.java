package edu.uci.ics.cloudberry.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by monique on 09/10/17.
 */

public class FileHelper {

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

    public static BufferedReader createGZipReader(String fileName) throws IOException {
        GZIPInputStream input = new GZIPInputStream(
                new FileInputStream(new File(fileName)));
        BufferedReader br = new BufferedReader(
                new InputStreamReader(input, "UTF-8"));
        return br;
    }
}
