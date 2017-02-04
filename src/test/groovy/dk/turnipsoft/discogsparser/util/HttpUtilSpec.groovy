package dk.turnipsoft.discogsparser.util

import dk.turnipsoft.discogsparser.model.Configuration
import groovy.json.JsonSlurper
import spock.lang.Specification

/**
 * Created by shartvig on 04/02/14.
 */
class HttpUtilSpec extends Specification {

    Object 'test getting a json release'() {
        given:
        HttpUtil httpUtil = new HttpUtil()
        JsonSlurper slurper = new JsonSlurper()

        when:
        String result = httpUtil.getJSONFromURL('http://api.discogs.com/releases/1039365')
        Map<String, Object> json = slurper.parseText(result)

        then:
        result != null
        json != null
    }

    Object 'test getting simple file'() {
        given:
        String url = 'http://s.pixogs.com/image/R-150-114443-001.jpg'
        HttpUtil httpUtil = new HttpUtil()
        httpUtil.userAgent = 'Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2'

        when:
        byte[] result = httpUtil.getBytesFromUrl(url)

        then:
        result
        result.size()>0

    }

    Object 'test getting simple file2'() {
        given:
        String url = 'http://s.pixogs.com/image/R-150-114443-001.jpg'
        HttpUtil httpUtil = new HttpUtil()
        httpUtil.userAgent = 'Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2'

        when:
        byte[] result = httpUtil.getBytesFromUrl3(url)

        then:
        result
        result.size()>0

    }

    void "test simple wget with header()"() {
        given:
        String url = "https://api.discogs.com/releases/1039365"
        HttpUtil httpUtil = new HttpUtil()
        Configuration configuration = new Configuration()

        when:
        String json = httpUtil.getJsonWithWget(url, configuration.token)

        then:
        json

        when:
        System.out.println(json)

        then:
        json.contains("images")
        json.contains("thumb")
    }
}


