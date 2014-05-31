package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.util.FileUtil

/**
 * Created by shartvig on 31/05/14.
 */
class SiteProcessor implements ListingProcessor {

    private static final String INCLUDE_CD = '$includeCd'
    private static final String INCLUDE_VINYL = '$includeVinyl'
    private static final String INCLUDE_CASSETTE = '$includeCassette'
    private static final String INCLUDE_BODY = '$includeBody'


    Configuration configuration
    String siteDir
    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
        this.siteDir = configuration.generateDirectory +"/site/"
    }

    @Override
    Object processListing(Listing listing) {
        return null
    }

    @Override
    void endProcessing(Context context) {
        generateSalesFile("cd")
        generateSalesFile("vinyl")
        generateSalesFile("cassette")
        copyContents('about.html')
        copyContents('forsendelse.html')
        copyContents('goldmine.html')
        copyContents('index.html')
        copySimple('plader.js')
        copySimple('plader.css')
    }

    void copyContents(String fileName) {
        String general = FileUtil.readFileFromClasspath('web/general_template.html')
        String toCopy = FileUtil.readFileFromClasspath("web/$fileName")

        String theFinal = general.replace('$includeBody',toCopy)

        FileUtil.writeFile(this.configuration.generateDirectory+"/site/$fileName", new StringBuffer(theFinal))
    }

    void copySimple(String fileName) {
        String toCopy = FileUtil.readFileFromClasspath("web/$fileName")

        FileUtil.writeFile(this.configuration.generateDirectory+"/site/$fileName", new StringBuffer(toCopy))
    }


    void generateSalesFile(String media ) {
        String template = 'web/'+media+'s_template.html'
        String salesFile = this.configuration.generateDirectory+'/'+media+'s_for_sale.html'

        String salesResultFileName = "/site/"+media+".html"

        String body = FileUtil.readFileFromClasspath(template)
        String sales = FileUtil.readFile(salesFile)
        String general = FileUtil.readFileFromClasspath('web/general_template.html')

        media = media.substring(0,1).toUpperCase() + media.substring(1)
        body = body.replace('$include'+media, sales)

        String theFinal = general.replace('$includeBody',body)
        FileUtil.writeFile(this.configuration.generateDirectory+salesResultFileName, new StringBuffer(theFinal))
    }
}
