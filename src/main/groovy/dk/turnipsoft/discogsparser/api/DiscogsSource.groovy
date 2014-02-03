package dk.turnipsoft.discogsparser.api

import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Release

/**
 * Created by shartvig on 03/02/14.
 */
public interface DiscogsSource {

    void init(Configuration configuration)
    Listing nextListing()
    boolean hasMoreListings()
    void loadListings()

    Release lookupRelease(Listing listing)

}