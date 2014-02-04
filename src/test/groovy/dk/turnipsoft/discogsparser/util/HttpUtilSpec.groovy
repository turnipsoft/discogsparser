package dk.turnipsoft.discogsparser.util

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
}


