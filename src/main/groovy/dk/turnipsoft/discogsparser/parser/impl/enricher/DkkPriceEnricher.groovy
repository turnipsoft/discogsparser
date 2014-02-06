package dk.turnipsoft.discogsparser.parser.impl.enricher

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 04/02/14.
 */
class DkkPriceEnricher implements ListingEnricher {

    Configuration configuration

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    double dkk = 7.5

    @Override
    void enrich(Listing listing, Context context) {
        int dkkPrice = listing.getPriceEur() * dkk
        if (dkkPrice<10) {
            dkkPrice = 5
        } else {
            dkkPrice = dkkPrice - (dkkPrice % 10)
        }

        listing.setPriceDkk(dkkPrice)
    }
}