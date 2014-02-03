package dk.turnipsoft.discogsparser.util

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.http.Consts
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder

/**
 * Created by shartvig on 03/02/14.
 */
class HttpUtil {

    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()

    private static final Log log = LogFactory.getLog(this)

    /**
     * Executes the given http get method.
     *
     * @param httpGet the http et method to execute
     * @return the http status code of the call, if the call times out -1 is returned.
     */
    HttpResponse httpClientExecute(HttpGet httpGet, HttpClientContext context) {
        int statusCode = -1
        HttpResponse response
        CloseableHttpClient httpClient = httpClientBuilder.build()
        try {
            if(context) {
                response = httpClient.execute(httpGet, context)
            } else {
                response =  httpClient.execute(httpGet)
            }
            statusCode = response.statusLine.statusCode
            log.trace("executing get call to URI:${httpGet.URI}")
        } catch (IOException e) {
            log.warn("exception occurred ${e} this is probably due to a timeout")
            throw e
        }
        httpClient.close()
        return response
    }

    @Override
    String sendHttpRequest(String url, Map<String, String> params) {
        URI uri = buildUri(url, params, false)
        HttpGet httpGet = prepareHttpGet(uri)
        HttpResponse response = httpClientExecute(httpGet, null)
        String responseContent = response.entity.content.getText(Consts.UTF_8.toString())
        return responseContent
    }

    @Override
    int sendHttpsRequest(String url, Map<String, String> params) {
        URI uri = buildUri(url, params, true)
        HttpGet httpGet = prepareHttpGet(uri)
        HttpResponse response = httpClientExecute(httpGet, null)
        String responseContent = response.entity.content.getText(Consts.UTF_8.toString())
        return responseContent
    }

    @Override
    int sendRequestBasicAuth(String url, Map<String, String> params, String username, String password) {
        URI uri = buildUri(url, params, true)
        HttpGet httpGet = prepareHttpGet(uri)
        HttpClientContext context = HttpClientContext.create()
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider()
        AuthScope authScope = new AuthScope(uri.getHost(), AuthScope.ANY_PORT)
        UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password)
        credentialsProvider.setCredentials(authScope, usernamePasswordCredentials)
        context.credentialsProvider = credentialsProvider
        return httpClientExecute(httpGet, context)
    }

    /**
     * Configures the returned HttpGet with timeout settings.
     *
     * @param uri The uri of the request
     * @return a configured HttpGet
     */
    private HttpGet prepareHttpGet(URI uri) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build()
        HttpGet httpGet = new HttpGet(uri)
        httpGet.config = requestConfig
        return httpGet
    }

    /**
     * Builds the uri of a request
     *
     * @param url the url of the request
     * @param params the parameters of the request
     * @param ssl indicates whether this is a ssl call or not.
     * @return The constructed URI
     */
    private URI buildUri(String url, Map<String, String> params, boolean ssl) {
        if(ssl) {
            url.replaceFirst("http://", "https://")
        } else {
            url.replaceFirst("https://", "http://")
        }
        URIBuilder uriBuilder = new URIBuilder(url)
        params.each { parameterName, value ->
            uriBuilder.addParameter(parameterName, value)
        }
        uriBuilder.build()
    }
}
