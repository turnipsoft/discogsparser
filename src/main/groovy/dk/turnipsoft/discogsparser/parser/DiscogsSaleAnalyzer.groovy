package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Medium
import dk.turnipsoft.discogsparser.model.Release
import dk.turnipsoft.discogsparser.model.ReleaseType
import dk.turnipsoft.discogsparser.parser.impl.processor.BIProcessor

/**
 * Created by shartvig on 09/07/14.
 */
class DiscogsSaleAnalyzer {

    Configuration configuration = new Configuration()

    private Listing readListingFromString(String s) {
        SimpleCsvTokenizer st = new SimpleCsvTokenizer(s)
        st.nextElement()
        String artist = st.nextElement()
        String title = st.nextElement()
        st.nextElement()
        String catno = st.nextElement()
        String format = st.nextElement()
        st.nextElement()
        String status = st.nextElement()
        String price = st.nextElement()

        Listing listing = new Listing()
        Release release = new Release()
        release.artistName = artist
        release.releaseName = title
        listing.description = "$artist - $title ($format)"
        if (format.contains("12") || format.contains("LP") || format.contains("7") || format.contains("10")) {
            release.medium = new Medium(ReleaseType.VINYLALBUM)
        } else if (format.contains("CD")) {
            release.medium = new Medium(ReleaseType.CDALBUM)
        } else if (format.contains("Cassette")) {
            release.medium = new Medium(ReleaseType.CASSETTE)
        } else if (format.contains("DVD") || format.contains("Bluray") || format.contains("DVDr") ) {
            release.medium = new Medium(ReleaseType.DVD)
        }

        listing.catalogNo = catno
        listing.forSale = status.equalsIgnoreCase("for sale")
        listing.priceEur = Double.valueOf(price)
        listing.priceDkk = listing.priceEur * 7.5
        listing.release = release

        return listing
    }

    private List<Listing> readListingsFromFile() {
        List<Listing> listings = []

        BufferedReader br = new BufferedReader(new FileReader(new File(configuration.generateDirectory+"/shartvig-inventory.csv")))
        String s = null
        boolean readFirst = false
        while ((s=br.readLine())!=null) {
            if (readFirst) {
                try {
                    Listing listing = readListingFromString(s)

                    if (!listing.forSale) {
                        System.out.println("adding listing : $listing.description")
                        listings << listing
                    }
                } catch (Exception e) {
                    System.out.println("troubles with $s - $e.message")
                }
            }
            readFirst = true
        }

        return listings
    }

    public void proces() {
        List<Listing> listings = readListingsFromFile()
        BIProcessor processor = new BIProcessor()
        processor.init(configuration)
        listings.each { Listing it ->
            processor.processListing(it)
        }

        processor.endProcessing(null)
    }

    public static void main(String [] args) {
        DiscogsSaleAnalyzer saleAnalyzer = new DiscogsSaleAnalyzer()
        saleAnalyzer.proces()
    }

}
