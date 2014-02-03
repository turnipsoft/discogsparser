package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.GenreType
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 03/02/14.
 */
class HtmlListingProcessor implements ListingProcessor {

    Configuration configuration

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
    }

    void addHtml(HtmlListing htmlListing, Map<GenreType,List<HtmlListing>> map) {
        List<HtmlListing> list = map.get(htmlListing.listing.release.genre)
        if (!list) {
            list = []
            map.put(htmlListing.listing.release.genre)
        }
        list.add(htmlListing)
    }



    private String toHtml(Listing listing) {
        return ''
    }
}
