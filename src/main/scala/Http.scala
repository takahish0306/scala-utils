package scala.utils

import java.util.ArrayList
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.http.HttpEntity
import org.apache.http.client.methods.{CloseableHttpResponse,HttpRequestBase,HttpGet,HttpPost}
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.impl.client.{CloseableHttpClient,HttpClients}
import scala.io.Source

object Http {

  /**
   * Returns the text (content) from a REST URL as a String by HTTP/GET.
   * Returns None if there was a problem.
   * This function will also throw exceptions if there are problems
   * trying to connect to the url.
   *
   * @param url String A complete URL, such as "http://foo.com/bar"
   * @param connectionTimeout Int The connection timeout, in ms.
   * @param socketTimeout Int The socket timeout, in ms.
   */
  def get(url: String, connectionTimeout: Int, socketTimeout: Int): (Int,String) = {
    val timeoutConfig = TimeoutConfig(connectionTimeout, socketTimeout)
    val httpGet = buildHttpGet(url, timeoutConfig)
    getContent[HttpGet](httpGet)
  }

  /**
   * Override get
   * Default connectionTimeout is 5000 and socketTimeout is 5000
   *
   * @param url String A complete URL, such as "http://foo.com/bar"
   */
  def get(url: String): (Int,String) = {
    get(url, 5000, 5000)
  }

  /**
   * Returns the text (content) from a REST URL as a String by HTTP/POST.
   * Returns None if there was a problem.
   * This function will also throw exceptions if there are problems
   * trying to connect to the url.
   *
   * @param url String A complete URL, such as "http://foo.com/bar"
   * @param params Map[String, String] Requests name-value parameters, such as Map("name" -> "value")
   * @param connectionTimeout Int The connection timeout, in ms.
   * @param socketTimeout Int The socket timeout, in ms.
   */
  def post(url: String, params: Map[String, String], connectionTimeout: Int, socketTimeout: Int): (Int,String) = {
    val timeoutConfig = TimeoutConfig(connectionTimeout, socketTimeout)
    val httpPost = buildHttpPost(url, params, timeoutConfig)
    getContent[HttpPost](httpPost)
  }

  /**
   * Override post
   * Default connectionTimeout is 5000 and socketTimeout is 5000
   *
   * @param url String A complete URL, such as "http://foo.com/bar"
   * @param params Map[String, String] Requests name-value parameters, such as Map("name" -> "value")
   */
  def post(url: String, params: Map[String, String]): (Int,String) = {
    post(url, params, 5000, 5000)
  }

  /**
   * Executes http request
   *
   * @param method A <: HttpRequestBase (HttpGet or HttpPost)
   */
  private def getContent[A <: HttpRequestBase](method: A): (Int,String) = {
    val httpClient = HttpClients.createDefault
    val response   = httpClient.execute(method)
    val entity     = response.getEntity

    var status  = -1
    var content = ""
    if (entity != null) {
      val inputStream = entity.getContent
      status = response.getStatusLine.getStatusCode
      content = Source.fromInputStream(inputStream).getLines.mkString
      inputStream.close
    } 

    response.close
    httpClient.close
    (status, content)
  }


  /**
   * Builds HttpGet object
   *
   * @param url String A comlete URL, such as "http://foo.com/bar"
   * @param timeoutConfig TimeoutConfig A timeout configuration
   */
  private def buildHttpGet(url: String, timeoutConfig: TimeoutConfig): HttpGet = {
    val method = new HttpGet(url)
    method.setConfig(buildRequestConfig(timeoutConfig))
    method
  }

  /**
   * Builds HttpPost object
   *
   * @param url String A comlete URL, such as "http://foo.com/bar"
   * @param params Map[String, String] Requests name-value parameters, such as Map("name" -> "value")
   * @param timeoutConfig TimeoutConfig A timeout configuration
   */
  private def buildHttpPost(url: String, params: Map[String, String], timeoutConfig: TimeoutConfig): HttpPost = {
    val method = new HttpPost(url)
    method.setConfig(buildRequestConfig(timeoutConfig))
    method.setEntity(buildUrlEncodedFormEntity(params))
    method
  }

  /**
   * A timeout configuration
   *
   * @param connectionTimeout Int The connection timeout, in ms.
   * @param socketTimeout The socket timeout, in ms.
   */
  private case class TimeoutConfig(connectionTimeout: Int, socketTimeout: Int)

  /**
   * Builds RequestConfig object
   *
   * @param timeoutConfig TimeoutConfig A timeout configuration
   */
  private def buildRequestConfig(timeoutConfig: TimeoutConfig): RequestConfig = {
    RequestConfig.custom
      .setConnectTimeout(timeoutConfig.connectionTimeout)
      .setSocketTimeout(timeoutConfig.socketTimeout)
      .build()
  }

  /**
   * Builds UrlEncodedFormEntity object
   *
   * @param params Map[String, String] Requests name-value parameters, such as Map("name" -> "value")
   */
  private def buildUrlEncodedFormEntity(params: Map[String, String]): UrlEncodedFormEntity = {
    // to convert into List[BasicNameValuePair]
    val list = for ((name, value) <- params) yield new BasicNameValuePair(name, value)
    // to convert into ArrayList[NameValuePair]
    val pairs = list.foldLeft(new ArrayList[NameValuePair])((pairs, pair) => { pairs.add(pair); pairs })

    new UrlEncodedFormEntity(pairs)
  }

}
