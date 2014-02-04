package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.api.ListingPersister
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.parser.impl.FileSourceImpl
import dk.turnipsoft.discogsparser.parser.impl.JsonListingPersister
import spock.lang.Specification

/**
 * Created by shartvig on 04/02/14.
 */
class JsonListingPersisterSpec extends Specification {

    Object 'test read and write'() {
        given:
        Context context = new Context()
        context.allListings = []
        Configuration configuration = new Configuration()
        configuration.databasename = 'test.json'
        ListingEnricher enricher = new JsonListingEnricher()
        FileSourceImpl fileSource = new FileSourceImpl()
        fileSource.init(configuration)
        ListingPersister persister = new JsonListingPersister()
        persister.init(configuration)

        List<Listing> readListings


        when:
        fileSource.loadListings()
        fileSource.listings.each { listing ->
            enricher.enrich(listing, context)
        }
        persister.persistListings(fileSource.listings)
        readListings = persister.restoreListings()

        then:
        fileSource.listings!=null
        fileSource.listings.first().release != null
        readListings!=null
        readListings.size() == fileSource.listings.size()

    }
}
