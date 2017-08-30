package integration

import com.google.gson.JsonObject
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils

/**
  * Created by monique on 29/08/17.
  */
class TextGeoLocatorIntegration {

  def geoTag(url: String, text: String): JsonObject = {

    val post = new HttpPost(url)

    val httpClient = HttpClients.createDefault()

    val requestJson: JsonObject = new JsonObject

    requestJson.addProperty("text", text)

    post.setHeader("Content-type", "application/json;charset=UTF-8")

    val stringRequest  = requestJson.toString
    post.setEntity(new StringEntity(stringRequest, "UTF-8"))

    val response = httpClient.execute(post)

    val entity = response.getEntity
    val responseString = EntityUtils.toString(entity, "UTF-8")

    return new JsonObject().getAsJsonObject(responseString)
  }
}
