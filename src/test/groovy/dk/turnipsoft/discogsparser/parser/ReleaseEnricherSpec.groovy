package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.parser.impl.FileSourceImpl
import spock.lang.Specification

/**
 * Created by shartvig on 03/02/14.
 */
class ReleaseEnricherSpec extends Specification {
    Object 'test json release enricher'() {
        given:
        Context context = new Context()
        context.allListings = []
        Configuration configuration = new Configuration()
        ListingEnricher enricher = new JsonListingEnricher()
        FileSourceImpl fileSource = new FileSourceImpl()
        fileSource.init(configuration)

        when:
        fileSource.loadListings()
        fileSource.listings.each { listing ->
            enricher.enrich(listing, context)
        }

        then:
        fileSource.listings!=null
        fileSource.listings.first().release != null


    }
}
