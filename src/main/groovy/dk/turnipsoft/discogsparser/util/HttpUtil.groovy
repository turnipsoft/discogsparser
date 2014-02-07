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

import java.nio.charset.Charset

/**
 * Created by shartvig on 03/02/14.
 */
class HttpUtil {

    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()

    private static final Log log = LogFactory.getLog(this)

    String userAgent = 'TurnipDiscogsClient/1.0 +http://turniprecords.org'

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

    String sendHttpRequest(String url, Map<String, String> params) {
        URI uri = buildUri(url, params, false)
        HttpGet httpGet = prepareHttpGet(uri)
        HttpResponse response = httpClientExecute(httpGet, null)
        String responseContent = response.entity.content.getText(Consts.UTF_8.toString())
        return responseContent
    }

    byte[] getBytesFromUrl2(String url) {
        URIBuilder uriBuilder = new URIBuilder(url)
        URI uri = uriBuilder.build()
        HttpGet httpGet = new HttpGet(uri)
        httpGet.setHeader("User-Agent",userAgent)
        HttpResponse response = httpClientExecute(httpGet, null)
        if (response.statusLine.statusCode!=200) {
            throw new IOException(String.valueOf(response.statusLine.statusCode))
        } else {
            InputStream is = response.entity.content
            ByteArrayOutputStream bytes = new ByteArrayOutputStream()
            byte b
            while ((b=is.read())!=-1) {
                bytes.write(b)
            }
            bytes.close()
            return bytes.toByteArray()
        }
    }

    byte[] getBytesFromUrl3(String url) {
        URIBuilder uriBuilder = new URIBuilder(url)
        URI uri = uriBuilder.build()
        HttpGet httpGet = new HttpGet(uri)
        httpGet.setHeader("User-Agent",userAgent)
        HttpResponse response = httpClientExecute(httpGet, null)
        if (response.statusLine.statusCode!=200) {
            throw new IOException(String.valueOf(response.statusLine.statusCode))
        } else {
            InputStream is = response.entity.content
            ByteArrayOutputStream bytes = new ByteArrayOutputStream()
            BufferedReader reader = new BufferedReader(new InputStreamReader(is))
            byte b
            while ((b=reader.read())!=-1) {
                bytes.write(b)
            }
            bytes.close()
            return bytes.toByteArray()
        }
    }

    byte[] getBytesFromUrl(String url) {
        URL u = new URL(url)
        URLConnection conn = u.openConnection()
        conn.addRequestProperty("User-Agent",
                userAgent);
        InputStream is = u.openStream();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream()
        byte b
        while ((b=is.read())!=-1) {
            bytes.write(b)
        }
        bytes.close()
        return bytes.toByteArray()
    }

    String getJSONFromURL(String url) {
        URL u = new URL(url)
        URLConnection conn = u.openConnection()
        conn.addRequestProperty("User-Agent",
                userAgent);
        InputStream is = u.openStream();
        int ptr = 0;
        StringBuffer buffer = new StringBuffer();
        while ((ptr = is.read()) != -1) {
            buffer.append((char)ptr);
        }
        String s = buffer.toString()
        s = new String(s.getBytes(),'UTF-8' )
        return s
    }


    int sendHttpsRequest(String url, Map<String, String> params) {
        URI uri = buildUri(url, params, true)
        HttpGet httpGet = prepareHttpGet(uri)
        HttpResponse response = httpClientExecute(httpGet, null)
        String responseContent = response.entity.content.getText(Consts.UTF_8.toString())
        return responseContent
    }

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
