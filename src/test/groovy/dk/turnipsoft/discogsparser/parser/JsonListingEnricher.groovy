package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.parser.impl.enricher.ReleaseEnricher
import dk.turnipsoft.discogsparser.util.ClasspathLoader
import groovy.json.JsonSlurper

/**
 * Created by shartvig on 03/02/14.
 */
class JsonListingEnricher extends ReleaseEnricher {

    String jsonString

    public JsonListingEnricher() {
        ClasspathLoader cpLoader = new ClasspathLoader()
        InputStream is = cpLoader.getInputStream('releases/release1.json')
        jsonString = is.getText('UTF-8')
    }

    @Override
    public Map<String, Object> getJSON(String url) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(jsonString)
    }

}
