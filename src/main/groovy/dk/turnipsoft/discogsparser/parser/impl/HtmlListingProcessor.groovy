package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.GenreType
import dk.turnipsoft.discogsparser.model.Listing
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by shartvig on 03/02/14.
 */
class HtmlListingProcessor implements ListingProcessor {

    Configuration configuration
    Logger logger = LoggerFactory.getLogger(HtmlListingProcessor.class)

    class HtmlListing {
        Listing listing
        String listingHtml
    }

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    Map<GenreType, List<HtmlListing>> cdHtml = [:]
    Map<GenreType, List<HtmlListing>> movieHtml = [:]
    Map<GenreType, List<HtmlListing>> cassetteHtml = [:]
    Map<GenreType, List<HtmlListing>> vinylHtml = [:]

    @Override
    Object processListing(Listing listing) {
        if (listing.release && listing.release.medium && listing.release.releaseName) {
            String html = toHtml(listing)
            HtmlListing htmlListing = new HtmlListing( ['listing':listing, 'listingHtml':html])
            if (listing.release.medium.CD) {
                addHtml(htmlListing, cdHtml)
            } else if (listing.release.medium.vinyl) {
                addHtml(htmlListing, vinylHtml)
            } else if (listing.release.medium.cassette) {
                addHtml(htmlListing, cassetteHtml)
            } else if (listing.release.medium.movie) {
                addHtml(htmlListing, movieHtml)
            }
            listing.processed = true
        } else {
            logger.warn("Unable to process: $listing.description")
            listing.errors.add('No release Information')
        }
    }

    void addHtml(HtmlListing htmlListing, Map<GenreType,List<HtmlListing>> map) {
        List<HtmlListing> list = map.get(htmlListing.listing.release.genre)
        if (!list) {
            list = []
            map.put(htmlListing.listing.release.genre.genreType, list)
        }
        list.add(htmlListing)
    }

    @Override
    void endProcessing() {
        System.out.println('Doing all the processing end magic')
    }

    private String toHtml(Listing listing) {
        return ''
    }
}
