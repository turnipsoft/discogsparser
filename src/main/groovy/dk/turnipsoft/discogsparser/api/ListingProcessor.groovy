package dk.turnipsoft.discogsparser.api

import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 03/02/14.
 */
public interface ListingProcessor {

    void init(Configuration configuration)
    Object processListing(Listing listing)
    void endProcessing(Context context)

}