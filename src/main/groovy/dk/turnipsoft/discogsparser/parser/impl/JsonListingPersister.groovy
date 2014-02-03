package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.api.ListingPersister
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Listing

/**
 * persists all listings to json
 */
class JsonListingPersister implements ListingPersister {

    Configuration configuration

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    @Override
    void persistListings(List<Listing> listings) {

    }
}
