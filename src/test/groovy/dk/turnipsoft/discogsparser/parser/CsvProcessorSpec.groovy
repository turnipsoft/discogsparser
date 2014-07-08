package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Genre
import dk.turnipsoft.discogsparser.model.GenreType
import dk.turnipsoft.discogsparser.model.Grading
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Medium
import dk.turnipsoft.discogsparser.model.Release
import dk.turnipsoft.discogsparser.model.ReleaseType
import dk.turnipsoft.discogsparser.parser.impl.processor.CsvProcessor
import spock.lang.Specification

/**
 * Created by shartvig on 08/07/14.
 */
class CsvProcessorSpec extends Specification {

    Object "testProcessor"() {
        given:
        Configuration config = new Configuration()
        CsvProcessor processor = new CsvProcessor()
        processor.filenameprefix = "test"

        Listing listing = new Listing()
        listing.description = "Telstar Sound Drone - Comedown (LP, Album)"
        listing.priceDkk = 190
        listing.discGrading = Grading.getGrading("Mint (M)", Grading.GRADING_DISC)
        listing.sleeveGrading = Grading.getGrading("Mint (M)", Grading.GRADING_SLEEVE)
        listing.release = new Release()
        listing.discGradingString = "Near Mint"
        listing.sleeveGradingString = "Near Mint"
        listing.release.artistName = "Telstar Sound Drone"
        listing.comment = "bare en feee plade"
        listing.release.genre = new Genre()
        listing.release.genre.genreType = GenreType.METAL
        listing.release.medium = new Medium(ReleaseType.VINYLALBUM)

        when:
        processor.init(config)
        processor.processListing(listing)
        processor.endProcessing(null)

        then:
        processor
    }
}
