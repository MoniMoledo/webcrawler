package edu.uci.ics.cloudberry.noah

import java.io.File

import edu.uci.ics.cloudberry.gnosis._
import edu.uci.ics.cloudberry.noah.adm.Tweet
import edu.uci.ics.cloudberry.util.Profile._
import twitter4j.TwitterObjectFactory

import scala.collection.mutable

/*
 * This file contains code that is based on https://github.com/ISG-ICS/cloudberry.
 *
 * Copyright: mixed. See noah/LICENSE for copyright and licensing information.
 */


object TwitterJSONTagBrToADM {
  // TODO reserve all the fields and just replace the date and geo location to different part.
  // and only change it to the accept asterix json format, then we can use the feed in Asterix to transfer the format
  // without to much effort.
  val shapeMap = mutable.Map.empty[TypeLevel, String]

  val usage =
    """
      |Usage: BrHierarchyBuilder -state /path/to/state.json -county /path/to/county.json -city /path/to/city.json
      |It will read the status from stdIn, geoTag city/county/state information, and then convert it to ADM format
    """.stripMargin

  def parseOption(list: List[String]) {
    list match {
      case Nil =>
      case "-h" :: tail => System.err.println(usage); System.exit(0)
      case "-country" :: value :: tail => shapeMap += BrCountryLevel -> value; parseOption(tail)
      case "-state" :: value :: tail => shapeMap += BrStateLevel -> value; parseOption(tail)
      case "-city" :: value :: tail => shapeMap += BrCityLevel -> value; parseOption(tail)
      case option :: tail => System.err.println("unknown option:" + option); System.err.println(usage); System.exit(1);
    }
  }

  def tagOneTweet(ln: String, brGeoGnosis: BrGeoGnosis) = {
    try {
      val adm = Tweet.toBrADM(TwitterObjectFactory.createStatus(ln), brGeoGnosis, true)
      if (adm.length > 0) println(adm)
    } catch {
      case e: Throwable => {
        e.printStackTrace(System.err)
        System.err.println(ln)
      }
    }
  }

  //TODO make a parallel version of this one
  def main(args: Array[String]): Unit = {
    parseOption(args.toList)
    val brGeoGnosis = profile("loading resource") {
      new BrGeoGnosis(shapeMap.mapValues(new File(_)).toMap)
    }
    for (ln <- scala.io.Source.stdin.getLines()) {
        tagOneTweet(ln, brGeoGnosis)
    }
  }
}
