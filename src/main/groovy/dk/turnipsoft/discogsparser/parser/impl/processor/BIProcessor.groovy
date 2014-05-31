package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.MasterReleaseType
import dk.turnipsoft.discogsparser.util.FileUtil

/**
 * Created by shartvig on 31/05/14.
 */
class BIProcessor implements ListingProcessor {

    Configuration configuration

    List<Listing> highValue = []
    List<Listing> mediumValue = []
    List<Listing> lowValue = []

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    @Override
    Object processListing(Listing listing) {
        if (!listing.forSale && listing.release.medium.masterReleaseType.equals((MasterReleaseType.CD))) {
            if (listing.priceDkk < 0.90) {
                lowValue.add(listing)
            } else if ( listing.priceDkk > 1.90 && listing.priceDkk < 6.90) {
                mediumValue.add(listing)
            } else if ( listing.priceDkk >= 6.95)  {
                highValue.add(listing)
            }
        }
    }

    @Override
    void endProcessing(Context context) {
        FileUtil.writeFile(this.configuration.generateDirectory+"/low_value_cds.txt", lowValue)
        FileUtil.writeFile(this.configuration.generateDirectory+"/medium_value_cds.txt", mediumValue)
        FileUtil.writeFile(this.configuration.generateDirectory+"/high_value_cds.txt", highValue)
    }
}
