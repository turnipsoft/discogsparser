package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.MasterReleaseType

/**
 * Created by shartvig on 31/05/14.
 */
class WorthProcessor implements ListingProcessor {

    double priceVinyl = 0
    double priceCd = 0

    @Override
    void init(Configuration configuration) {

    }

    @Override
    Object processListing(Listing listing) {
        if (listing.release && listing.release.medium && listing.forSale && listing.release && listing.release.medium.masterReleaseType == MasterReleaseType.CD) {
            priceCd += listing.priceDkk
        } else if (listing.release && listing.release.medium && listing.forSale && listing.release && listing.release.medium.masterReleaseType == MasterReleaseType.VINYL) {
            priceVinyl += listing.priceDkk
        }
    }

    @Override
    void endProcessing(Context context) {
        System.out.println("CD's worth : $priceCd")
        System.out.println("Vinyl's worth : $priceVinyl")
    }
}
