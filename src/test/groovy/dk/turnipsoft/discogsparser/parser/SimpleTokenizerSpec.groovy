package dk.turnipsoft.discogsparser.parser

import spock.lang.Specification

/**
 * Created by shartvig on 09/07/14.
 */
class SimpleTokenizerSpec extends Specification {

    Object "test simple csv"() {
        given:
        String csv = 'felt1,felt2,"felt3","felt4"'
        SimpleCsvTokenizer c = new SimpleCsvTokenizer(csv)

        when:
        String s = c.nextElement()

        then:
        s=='felt1'

        when:
        String s2 = c.nextElement()

        then:
        s2=='felt2'

        when:
        String s3 = c.nextElement()

        then:
        s3=='felt3'

        when:
        String s4 = c.nextElement()

        then:
        s4=='felt4'


    }

}
