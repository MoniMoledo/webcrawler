package integration

import com.google.gson.{Gson, JsonElement, JsonObject}
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils


/**
  * Created by monique on 29/08/17.
  */
class TextGeoLocatorIntegration {

  def geoTag(url: String, text: String): Unit = {

    // convert it to a JSON string
    val requestJson: JsonObject = new JsonObject

    requestJson.addProperty("text", text)

    // create an HttpPost object
    val post = new HttpPost(url)

    // set the Content-type
    post.setHeader("Content-type", "application/json;charset=UTF-8")

    // add the JSON as a StringEntity
    val stringRequest  = requestJson.toString
    post.setEntity(new StringEntity(stringRequest, "UTF-8"))
    // send the post request4
    val httpClient = HttpClients.createDefault()
    val response = httpClient.execute(post)

    // print the response headers
    println("--- HEADERS ---")
    response.getAllHeaders.foreach(arg => println(arg))
    val entity = response.getEntity
    val responseString = EntityUtils.toString(entity, "UTF-8")
    System.out.println(responseString)
  }
}
