package dk.turnipsoft.discogsparser.parser.impl.enricher

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 03/02/14.
 */
class PriceEnricher implements ListingEnricher {
    Configuration configuration

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    @Override
    void enrich(Listing listing, Context context) {

    }
}
