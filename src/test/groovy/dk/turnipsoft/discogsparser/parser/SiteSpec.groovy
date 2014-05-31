package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.parser.impl.processor.SiteProcessor
import spock.lang.Specification

/**
 * Created by shartvig on 31/05/14.
 */
class SiteSpec extends Specification {

    Object 'testSite'() {
    given:

    when:
    Configuration configuration = new Configuration()
    SiteProcessor siteProcessor = new SiteProcessor()
    siteProcessor.init(configuration)
    siteProcessor.endProcessing()

    then:
    siteProcessor

    }
}
