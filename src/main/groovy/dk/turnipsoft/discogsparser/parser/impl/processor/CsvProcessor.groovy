package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.GenreType
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.util.FileUtil
import org.slf4j.LoggerFactory

/**
 * A CSV processor
 */
class CsvProcessor implements ListingProcessor {

    org.slf4j.Logger logger = LoggerFactory.getLogger(CsvProcessor.class)
    String csvHeader = "Band or Artist,Title,Disc Grading,Sleeve Grading,Comments,Mediatype,Genre,Price"

    List<String> fullCsv = new ArrayList<>()
    List<String> fullMetalCsvCd = new ArrayList<>()
    List<String> fullMetalCsvVinyl = new ArrayList<>()

    String filenameprefix = ""

    Configuration configuration
    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    String cutDescription(String description) {
        String s = description.substring(description.indexOf("- ")+2)
        if (s.contains("(")) {
            s = s.substring(0, s.indexOf("(")-1)
        }
        return s
    }

    @Override
    Object processListing(Listing listing) {
        if (listing.release && listing.release.genre && listing.release.medium) {
            String title = cutDescription(listing.description)
            String media = listing.release.medium.masterReleaseType.name()
            String genre = listing.release.genre.genreType.name()
            int price = listing.priceDkk
            String csv = "\"$listing.release.artistName\",\"$title\",\"$listing.discGradingString\",\"$listing.sleeveGradingString\",\"$listing.comment\",\"$media\",\"$genre\",\"$price\""
            fullCsv << csv
            if (listing.release.medium.CD && listing.release.genre.genreType == GenreType.METAL) {
                fullMetalCsvCd << csv
            }
            if (listing.release.medium.vinyl && listing.release.genre.genreType == GenreType.METAL) {
                fullMetalCsvVinyl << csv
            }
        } else {
            logger.error("unable to process : $listing.description")
        }
    }

    @Override
    void endProcessing(Context context) {
        fullCsv = fullCsv.sort()
        fullMetalCsvCd = fullMetalCsvCd.sort()
        fullMetalCsvVinyl = fullMetalCsvVinyl.sort()

        FileUtil.writeFile(this.configuration.generateDirectory+"/$filenameprefix"+"plader.csv", fullCsv)
        FileUtil.writeFile(this.configuration.generateDirectory+"/$filenameprefix"+"metal_cd.csv", fullMetalCsvCd)
        FileUtil.writeFile(this.configuration.generateDirectory+"/$filenameprefix"+"metal_vinyl.csv", fullMetalCsvCd)
    }
}
