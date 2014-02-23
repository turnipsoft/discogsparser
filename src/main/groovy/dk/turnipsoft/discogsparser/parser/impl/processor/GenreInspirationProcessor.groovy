package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.GenreType
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.parser.impl.HtmlListingProcessor
import dk.turnipsoft.discogsparser.util.FileUtil

/**
 * Created by shartvig on 08/02/14.
 */
class GenreInspirationProcessor implements ListingProcessor {

    Configuration configuration
    Context context

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    @Override
    Object processListing(Listing listing) {

    }

    @Override
    void endProcessing(Context context) {
        this.context = context

        List<String> sales = []
        configuration.artistSalesList.each { p->
            if (p.value=='CD') {
                sales.addAll( getCDList(context, p.key))
            } else if (p.value=='VINYL') {
                sales.addAll(getVinylList(context, p.key))
            } else if (p.value=='ALL') {
                sales.addAll(getVinylList(context, p.key))
                sales.addAll(getCDList(context, p.key))
            }

            String fileName = configuration.generateDirectory + "/" +p.key +"_sales.txt"
            FileUtil.writeFile(fileName, sales)
            sales = []
        }

    }

    private String getArtistName(String artistName) {
        artistName = artistName.replace('_',' ')
        if (artistName.endsWith(' The')) {
            artistName = artistName.substring(0, artistName.length()-4)
            artistName = 'The '+artistName
        }
        return artistName
    }

    public List<String> getCDList(Context context, String artistName) {
        String formattedArtistName = getArtistName(artistName)
        List<String> list = []
        list << "CD'er med $formattedArtistName til salg: \n"
        list << "\n"
        list.addAll(getSalesLines(context.htmlListingProcessor.cdHtml, artistName))
        list << "\n"
        return list
    }

    public List<String> getVinylList(Context context, String artistName) {
        String formattedArtistName = getArtistName(artistName)
        List<String> list = []
        list << "Vinyler med $formattedArtistName til salg:"
        list << ""
        list.addAll(getSalesLines(context.htmlListingProcessor.vinylHtml, artistName))
        list << ""
        return list
    }



    List<String> getSalesLines(Map<GenreType, List<HtmlListingProcessor.HtmlListing>> listingsMap, String artistName) {
        List<String> salesLines = []
        listingsMap.each { entry->
            List<HtmlListingProcessor.HtmlListing> listings = entry.value
            List<HtmlListingProcessor.HtmlListing> foundListings = listings.findAll({ it.listing.release && configuration.getGenreName(it.listing.release.artistName)==configuration.getGenreName(artistName) } )
            foundListings.each {
                Listing listing = it.listing
                int price = listing.priceDkk
                String desc = context.htmlListingProcessor.cutDescription(listing.description)
                salesLines << "$desc     $listing.discGradingString / $listing.sleeveGradingString      $price,-\n"
            }
        }
        return salesLines
    }
}
