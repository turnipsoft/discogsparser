package dk.turnipsoft.discogsparser.parser.impl.enricher

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by shartvig on 07/02/14.
 */
class ArtistEnricher implements ListingEnricher {

    Logger logger = LoggerFactory.getLogger(ArtistEnricher.class)

    @Override
    void init(Configuration configuration) {

    }

    @Override
    void enrich(Listing listing, Context context) {
        if (listing.release) {
            String artistName = listing.release.jsonMap.get('artists').get(0).get('name')
            if (artistName.endsWith(")") && !artistName.startsWith("Sunn")) {
                logger.debug("changing artist name from : "+artistName)
                artistName = artistName.substring(0,artistName.lastIndexOf("(")-1);
                logger.debug("to : $artistName")
            }
            listing.release.artistName = artistName
        }
    }
}
