package edu.uci.ics.cloudberry.noah.feed

import java.io.File

import edu.uci.ics.cloudberry.gnosis._
import edu.uci.ics.cloudberry.noah.adm.{UnknownPlaceException, Tweet}
import edu.uci.ics.cloudberry.util.Profile._
import twitter4j.{TwitterException, TwitterObjectFactory}

/*
 * This file contains code that is based on https://github.com/ISG-ICS/cloudberry.
 *
 * Copyright: mixed. See noah/LICENSE for copyright and licensing information.
 */

object TagBrTweet {
  var shapeMap = Seq( BrCountryLevel -> "gnosis/src/main/resources/data/br_country.json",
    BrStateLevel -> "gnosis/src/main/resources/data/br_state.json",
    BrCityLevel -> "gnosis/src/main/resources/data/br_city.json").toMap

  val brGeoGnosis = profile("loading resource") {
    new BrGeoGnosis(shapeMap.mapValues(new File(_)).toMap)
  }

  @throws[UnknownPlaceException]
  @throws[TwitterException]
  def tagOneTweet(ln: String, requireGeoField: Boolean): String = {
    val adm = Tweet.toBrADM(TwitterObjectFactory.createStatus(ln), brGeoGnosis, requireGeoField)
    return adm
  }
}
