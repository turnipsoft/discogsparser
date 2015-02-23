package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Release
import dk.turnipsoft.discogsparser.parser.impl.processor.MissingImageProcessor
import spock.lang.Specification

/**
 * Created by SorenHartvig on 22/02/15.
 */
class MissingImageProcessorSpec extends Specification {

    void "test missing image"() {
        given:
        Context context = new Context()
        Configuration configuration = new Configuration()
        MissingImageProcessor processor = new MissingImageProcessor()
        processor.init(configuration)

        Listing l = new Listing()
        l.release = new Release()
        l.release.imageFileName = "notthere.jpeg"
        l.description = "TEST"

        when:
        processor.processListing(l)
        processor.endProcessing(context)

        then:
        processor.wgets
        processor.wgets.size() == 2
        processor.wgets.get(1) == "wget --user-agent firefox http://s.pixogs.com/image/notthere.jpeg -O notthere.jpeg"
    }
}
