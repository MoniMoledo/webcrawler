package integration

import java.util.logging.{Level, Logger}

import com.google.gson.JsonObject
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import util.FileLogger

/**
  * Created by monique on 29/08/17.
  */
class TextGeoLocatorIntegration {

  private val logger = FileLogger.getLogger

  def geoTag(url: String, text: String): String = {

    val post = new HttpPost(url)

    val httpClient = HttpClients.createDefault()

    val requestJson: JsonObject = new JsonObject

    requestJson.addProperty("text", text)

    post.setHeader("Content-type", "application/json;charset=UTF-8")

    val stringRequest  = requestJson.toString
    post.setEntity(new StringEntity(stringRequest, "UTF-8"))

    val response = httpClient.execute(post)

    val statusCode = response.getStatusLine.getStatusCode

    val entity = response.getEntity

    val responseString = EntityUtils.toString(entity, "UTF-8")

    if(statusCode != 200){
      logger.log(Level.SEVERE, responseString)
      return null
    }

    return responseString
  }
}
