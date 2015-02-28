package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Created by SorenHartvig on 22/02/15.
 */
class MissingImageProcessor implements ListingProcessor {

    public Logger log = LoggerFactory.getLogger( MissingImageProcessor.class )
    Configuration configuration

    List<String> files
    List<String> wgets
    String header

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
        this.files = []
        this.wgets = []
        this.header = (configuration.token) ? "--header=\"Authorization: Discogs token=$configuration.token\"" : ""
    }

    @Override
    Object processListing(Listing listing) {
        String filedir = this.configuration.generateDirectory+"/images"
        if (!listing.release.imageFileName) {
            log.debug("no image on $listing.description")
        }

        if (listing.release?.imageFileName) {

            String fullPath = filedir + "/" +listing.release.imageFileName
            File f = new File(fullPath)
            if (!f.exists() || f.size()==0) {
                log.debug("listing $listing.description is missing its file $listing.release.imageFileName")
                String fullFilename = "$configuration.generateDirectory/images/$listing.release.imageFileName"

                wgets << "sleep 2"

                wgets << "wget --user-agent dp $header \"$listing.release.publicImageUrl\" -O $fullFilename"
            } else {
                files << "$listing.description --> $listing.release.imageFileName --> ${f.size()}"
            }
        }
        return null
    }

    @Override
    void endProcessing(Context context) {
        context.allListings.each { listing->
            processListing(listing)
        }
        writeFile("getMissingImages.sh", wgets)
        writeFile("fileStatus.txt", files)
    }

    private void writeFile(String file, List<String> l) {
        File f = new File(configuration.generateDirectory+"/"+file)

        PrintWriter pw = new PrintWriter(new FileWriter(f))
        l.each { wget->
            pw.println(wget)
        }

        pw.close()
    }
}
