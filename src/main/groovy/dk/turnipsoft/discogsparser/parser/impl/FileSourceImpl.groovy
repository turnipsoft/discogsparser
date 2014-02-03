package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.util.ClasspathLoader
import groovy.json.JsonSlurper

/**
 * Created by shartvig on 03/02/14.
 */
class FileSourceImpl extends DiscogsSourceImpl {

    @Override
    void loadListings() {
        ClasspathLoader cpLoader = new ClasspathLoader()
        JsonSlurper jsonSlurper = new JsonSlurper()
        int c = 1
        try {
            for (i in 1..50) {
                c=i
                InputStream input = cpLoader.getInputStream("$configuration.listingDirectory/listing.${i}.json")
                Map<String, Object> jsonMap = jsonSlurper.parseText(input.getText('UTF-8'))
                List<Map<String, Object>> jsonListings = jsonMap.get('listings')
                jsonListings.each { listing ->
                    addListingFromJson(listing)
                }
            }
        } catch (IllegalArgumentException iae) {
            System.out.println("end reading at ${c} no more files")
        }

    }



}
