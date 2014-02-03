package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.api.DiscogsSource
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Grading
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Release

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

    @Override
    void loadListings() {

    }

    @Override
    Listing nextListing() {
        return null
    }

    @Override
    boolean hasMoreListings() {
        return false
    }

    @Override
    Release lookupRelease(Listing listing) {
        return null
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

        listings.add(listing)
        return listing
    }
}
