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

    //String userAgent = 'PladerDiscogsReader/1.0 +http://plader.nu'

    String userAgent = 'curl/7.30.0'

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
        String responseContent = readContent(response.entity.content)
        return responseContent
    }

    String readContent(InputStream i) {
        BufferedReader br = new BufferedReader(new InputStreamReader(i))
        String s = null
        StringBuffer b = new StringBuffer()
        while ((s=br.readLine())!=null) {
            b.append(s)
        }
        return b.toString()
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
        Thread.sleep(1000)

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
        Thread.sleep(1000)
        URL u = new URL(url)
        URLConnection conn = u.openConnection()
        addHeaders(conn)

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

    private void addHeaders(URLConnection conn) {
        //userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:27.0) Gecko/20100101 Firefox/27.0"

        conn.addRequestProperty("User-Agent",
                userAgent);
        //conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        /*conn.addRequestProperty("Accept-Encoding", "gzip,deflate")
        conn.addRequestProperty("Pragma", "no-cache")
        conn.addRequestProperty("Cache-Control", "no-cache")
        conn.addRequestProperty("Accept-Language","en-US,en;q=0.5")
        conn.addRequestProperty("Host", "api.discogs.com")
        conn.addRequestProperty("Cookie", "sid=8eb952a5a718defda61b8b06759143a9; __utma=15419939.1274148246.1365163038.1391704993.1393137793.18; __utmz=15419939.1365163038.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); _ga=GA1.2.1274148246.1365163038; __gads=ID=62c6889a10935f48:T=1365163038:S=ALNI_MZTm79_-2oohAttsx_rJ-nkqUeHFA; mp_session=061f28051f9f18049f22dd2b; ck_username=shartvig")
        */
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
        httpGet.addHeader("UserAgent", "discogsparser1.0")
        httpGet.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        httpGet.addHeader("Accept-Encoding", "gzip, deflate")
        httpGet.addHeader("Host", "api.discogs.com")
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

    public static void main(String []args) {

        HttpUtil hu = new HttpUtil()

        //String result = hu.sendHttpRequest("http://api.discogs.com/releases/1395980",[:])
        String result = hu.getJSONFromURL("http://api.discogs.com/releases/1395980")
    }

    public String getJsonWithWget(String url, String token) {
        String h = (token) ? "--header=\"Authorization: Discogs token=$token\"" : ""
        Runtime rt = Runtime.getRuntime();
        String command = "/usr/local/bin/wget $h \"$url\" -O /tmp/result.json"
        String cmd = "/tmp/get.sh"
        writeFile("get.sh", command)
        System.out.println("invoking $command")
        Process ps = rt.exec(cmd);
        ps.waitFor()
        System.out.println("ps endded with "+ps.exitValue())
        System.out.println(ps.inputStream.text)
        System.out.println(ps.err.text)

        return readFile("/tmp/result.json")
    }

    static String httpsify(String url) {
        return url.replace("http","https")
    }

    public void getResourceWithWget(String url, String token) {
        String h = (token) ? "--header=\"Authorization: Discogs token=$token\"" : ""
        Runtime rt = Runtime.getRuntime();
        String command = "/usr/local/bin/wget $h $url -O /tmp/image.jpeg"
        String cmd = "/tmp/get.sh"
        writeFile("get.sh", command)
        System.out.println("invoking $command")
        Process ps = rt.exec(cmd);
        ps.waitFor()
        System.out.println("ps endded with "+ps.exitValue())
        System.out.println(ps.inputStream.text)
        System.out.println(ps.err.text)
    }

    private void writeFile(String filename, String command) {
        File f = new File("/tmp/$filename")
        PrintWriter pw = new PrintWriter(new FileWriter(f))
        pw.write(command)
        pw.close()
        chmod(f.absolutePath)
    }

    private void chmod(String filename) {
        Runtime rt = Runtime.getRuntime();
        String command = "chmod 755 $filename"
        Process ps = rt.exec(command);
        ps.waitFor()
    }

    private String readFile(String filename) {
        BufferedReader br = new BufferedReader(new FileReader(filename))
        String line = null
        StringBuffer buf = new StringBuffer()
        while ((line = br.readLine()) != null) {
            buf.append(line)
        }

        return buf.toString()
    }
}
