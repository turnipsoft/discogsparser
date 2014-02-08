package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.api.DiscogsSource
import dk.turnipsoft.discogsparser.api.ListingPersister
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by shartvig on 03/02/14.
 */
class DiscogsParser {

    Logger logger = LoggerFactory.getLogger(DiscogsParser.class)


    void status(String status) {
        System.out.println(status)
    }

    public void run() {
        status('Starting discogsparser')
        Context context = new Context()
        Configuration configuration = new Configuration()
        // load database if any
        ListingPersister persister = configuration.persisters.get(0)
        List<Listing> allListings = persister.restoreListings()
        context.allListings = allListings

        status("Loaded "+allListings.size()+" listings from database")
        DiscogsSource source = configuration.source
        source.loadListings()
        status("Processing "+source.listings.size()+" listings from DiscogsSource:$source")

        source.listings.each { listing->
            // enrich
            configuration.enrichers.each { enricher->
                enricher.enrich(listing, context)
            }
            // process
            configuration.processors.each { processor->
                processor.processListing(listing)
            }
        }

        // end proces
        // process
        status('End Processing')
        configuration.processors.each { processor->
            processor.endProcessing(context)
        }

        // persist
        status('Persisting')
        configuration.persisters.each { p->
            p.persistListings(source.listings)
        }

        status('Done, Enjoy!')

    }

    public static void main(String [] args) {
        DiscogsParser dp = new DiscogsParser()
        dp.run()

    }
}
