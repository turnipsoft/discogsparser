package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.util.HttpUtil

/**
 * Created by shartvig on 06/02/14.
 */
class ImageFetchProcessor implements ListingProcessor {

    Configuration configuration
    String imageDir = "images"
    HttpUtil httpUtil = new HttpUtil()
    List<String> curls = []
    String token
    String header

    void initImageDir() {
        File imageDirectory = new File(configuration.generateDirectory+"/"+imageDir)
        if (!imageDirectory.exists()) {
            System.out.println('Making images dir')
            imageDirectory.mkdir()
        }
    }

    private void writeCurlFile() {
        File f = new File(configuration.generateDirectory+"/getImages.sh")

        PrintWriter pw = new PrintWriter(new FileWriter(f))
        curls.each { curl->
            pw.println(curl)
        }

        pw.close()
    }

    private boolean fileExists(String filename) {
        String fullFilename = "$configuration.generateDirectory/$imageDir/$filename"
        File f = new File(fullFilename)
        return f.exists()
    }

    private void fetchFile(String filename, Listing listing) {

        String fullFilename = "$configuration.generateDirectory/$imageDir/$filename"
        if (listing.release.publicImageUrl && !listing.release.publicImageUrl.contains('pixogs')) {
            curls << "sleep 2"
            curls << "wget --no-check-certificate --user-agent dp $header \"$listing.release.publicImageUrl\" -O $fullFilename"
        }

        return
    }

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
        initImageDir()
        this.token = configuration.token
        this.header = (token) ? "--header=\"Authorization: Discogs token=$token\"" : ""
    }

    @Override
    Object processListing(Listing listing) {
        if (listing.release) {
            if (listing.release.imageFileName && !fileExists(listing.release.imageFileName)) {
                System.out.println("go get file : "+listing.release.imageFileName)
                fetchFile(listing.release.imageFileName, listing)
            }
        }
    }

    @Override
    void endProcessing(Context context) {
        System.out.println("missed out "+curls.size() + " images, so making a curl script for those")
        writeCurlFile()
    }
}
