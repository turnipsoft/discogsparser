package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 04/02/14.
 */
class ErrorProcessor implements ListingProcessor{

    List<Listing> listingsInError = []

    @Override
    void init(Configuration configuration) {

    }

    @Override
    Object processListing(Listing listing) {
        if (listing.errors.size()>0) {
            listingsInError << listing
        }
    }

    @Override
    void endProcessing() {
        listingsInError.each { listing->
            System.out.println("listing in error : $listing.description")
            listing.errors.each { error ->
                System.out.println(" - $error")
            }
        }

    }
}
