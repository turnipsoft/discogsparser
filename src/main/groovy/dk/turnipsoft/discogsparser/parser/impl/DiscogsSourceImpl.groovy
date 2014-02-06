package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.api.DiscogsSource
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Grading
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Release
import dk.turnipsoft.discogsparser.util.HttpUtil
import groovy.json.JsonSlurper

/**
 * Created by shartvig on 03/02/14.
 */
class DiscogsSourceImpl implements DiscogsSource {

    Configuration configuration
    List<Listing> listings = []
    int pageSize = 100

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration

    }

    void loadListings(List<Map<String, Object>> list) {
        list.each { listing ->
            addListingFromJson(listing)
        }
    }

    @Override
    void loadListings() {
        String listingsUrl = configuration.listingurl.replace('$username',configuration.username)

        HttpUtil httpUtil = new HttpUtil()
        String json = httpUtil.getJSONFromURL(listingsUrl)
        JsonSlurper slurper = new JsonSlurper()
        Map<String, Object> result = slurper.parseText(json)
        String nextUrl = result.get('pagination').get('urls').get('next')

        loadListings(result.get('listings'))

        while (nextUrl && nextUrl.length()>0) {
            System.out.println("Fecthing next page : $nextUrl")
            json = httpUtil.getJSONFromURL(nextUrl)
            result = slurper.parseText(json)
            nextUrl = result.get('pagination').get('urls').get('next')

            loadListings(result.get('listings'))
        }

    }

    @Override
    List<Listing> getListings() {
        return listings
    }

    Listing addListingFromJson(Map<String, Object> jsonMap) {
        Listing listing = new Listing();
        listing.comment = jsonMap.get('comments')
        listing.description = jsonMap.get('release').get('description')
        listing.priceEur = jsonMap.get('price').get('value')
        listing.releaseUrl = jsonMap.get('release').get('resource_url')
        listing.forSale = jsonMap.get('status') == 'For Sale'
        listing.sleeveGrading = Grading.getGrading(jsonMap.get('sleeve_condition'), Grading.GRADING_SLEEVE)
        listing.discGrading = Grading.getGrading(jsonMap.get('condition'), Grading.GRADING_DISC)
        listing.sleeveGradingString = jsonMap.get('sleeve_condition')
        listing.discGradingString = jsonMap.get('condition')
        listing.id = jsonMap.get('id')
        listing.catalogNo = jsonMap.get('release').get('catalog_number')

        listings.add(listing)
        return listing
    }
}
