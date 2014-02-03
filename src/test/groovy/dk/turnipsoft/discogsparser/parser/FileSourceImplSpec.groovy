package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.parser.impl.FileSourceImpl
import spock.lang.Specification

/**
 * Created by shartvig on 03/02/14.
 */
class FileSourceImplSpec extends Specification {

    Object 'test from files in classpath'() {
        given:
        Configuration configuration = new Configuration()
        FileSourceImpl fileSource = new FileSourceImpl()
        fileSource.configuration = configuration

        when:
        fileSource.loadListings()

        then:
        fileSource.listings != null
        fileSource.listings.size() > 80


    }
}
