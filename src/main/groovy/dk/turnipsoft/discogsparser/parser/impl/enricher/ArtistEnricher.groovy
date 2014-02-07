package dk.turnipsoft.discogsparser.parser.impl.enricher

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 07/02/14.
 */
class ArtistEnricher implements ListingEnricher {

    @Override
    void init(Configuration configuration) {

    }

    @Override
    void enrich(Listing listing, Context context) {
        String artistName = listing.release.jsonMap.get('artists').get(0).get('name')
        if (artistName.endsWith(")") && !artistName.startsWith("Sunn")) {
            artistName = artistName.substring(0,artistName.indexOf("("));
        }
        listing.release.artistName = artistName
    }
}
