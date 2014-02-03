package dk.turnipsoft.discogsparser.api

import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 03/02/14.
 */
public interface ListingEnricher {

    void init(Configuration configuration)
    void enrich(Listing listing)

}