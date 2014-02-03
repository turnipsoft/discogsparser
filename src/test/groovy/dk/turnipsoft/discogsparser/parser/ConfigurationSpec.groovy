package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.model.Configuration
import spock.lang.Specification

/**
 * Created by shartvig on 03/02/14.
 */
class ConfigurationSpec extends Specification {

    Object "Test Default Configuration"() {
        given:

        when:
        Configuration configuration = new Configuration()

        then:
        configuration.enrichers != null
        configuration.enrichers.size() == 3
        configuration.username == 'shartvig'
        configuration.processors != null
        configuration.processors.size() == 1
        configuration.persisters != null
        configuration.persisters.size() == 1
        configuration.listingDirectory == 'listings'

    }
}
