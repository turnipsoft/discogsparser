package dk.turnipsoft.discogsparser.parser.impl.enricher

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Release
import dk.turnipsoft.discogsparser.util.HttpUtil
import groovy.json.JsonSlurper

/**
 * Created by shartvig on 03/02/14.
 */
class ReleaseEnricher implements ListingEnricher {

    Configuration configuration

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    Listing findListing(Context context, Listing listing) {
        List<Listing> listings = context.allListings.collect {it.id==listing.id}
        if (listings.size()==0) {
            return null
        } else {
            return listings.first()
        }
    }

    public Map<String, Object> getJSON(String url) {
        HttpUtil h = new HttpUtil()
        String jsonString = h.sendHttpRequest(url, [:])
        JsonSlurper jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(jsonString)
    }

    public void enrichListing(Listing listing) {
        Release release = new Release()
        Map<String, Object> jsonMap = getJSON(listing.releaseUrl)
        release.releaseName = jsonMap.get('title')
        release.year = jsonMap.get('year')
        release.releaseDate = jsonMap.get('released')
        release.artistName = jsonMap.get('artists').get(0).get('name')
        listing.release = release
    }

    @Override
    void enrich(Listing listing, Context context) {
        Listing existingListing = findListing(context, listing)
        if (!existingListing || !existingListing.release || !existingListing.release.releaseName) {
            //enrich release
            enrichListing(listing)
        } else {
            existingListing.setPriceEur(listing.priceEur)
            listing.setRelease(existingListing.release)
        }
    }
}
