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
        curls << "sleep 2"
        curls << "wget --user-agent firefox $configuration.imageBaseUrl$filename -O $fullFilename"

        return

        if (filename) {
            //System.out.println("fetching image : $filename for listing: $listing.description")
            try {
                byte[] imageBytes = httpUtil.getBytesFromUrl(configuration.imageBaseUrl+filename)
                File f = new File(fullFilename)
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(f))
                dos.write(imageBytes)
                dos.close()
            } catch (IOException ioe) {
                //System.out.println("Can't fetch image: "+ioe.message)
                listing.errors.add('Unable to fetch image:'+configuration.imageBaseUrl+filename)
                curls << "curl $configuration.imageBaseUrl$filename > $fullFilename"
            }
        }
    }

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
        initImageDir()
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
