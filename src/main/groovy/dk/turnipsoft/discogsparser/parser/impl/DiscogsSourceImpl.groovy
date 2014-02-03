package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.api.DiscogsSource
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Release

/**
 * Created by shartvig on 03/02/14.
 */
class DiscogsSourceImpl implements DiscogsSource {

    Configuration configuration
    List<Listing> listings
    int pageSize = 100

    public DiscogsSourceImpl(Configuration configuration) {
        this.configuration = configuration
    }

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration

    }

    private void loadListings() {

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
}
