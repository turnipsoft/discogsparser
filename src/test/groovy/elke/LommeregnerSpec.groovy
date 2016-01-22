package elke

import spock.lang.Specification

/**
 * Created by SorenHartvig on 16/12/15.
 */
class LommeregnerSpec extends Specification {

    Lommeregner lommeregner

    void setup() {
        lommeregner = new Lommeregner()
    }

    void "test at plus virker som det skal"() {
        when:
        int resultat = lommeregner.plus(1,2)

        then:
        resultat == 5
    }

    void "test at minus virker som det skal"() {
        when:
        int resultat = lommeregner.minus(4,3)

        then:
        resultat == 1
    }

}
