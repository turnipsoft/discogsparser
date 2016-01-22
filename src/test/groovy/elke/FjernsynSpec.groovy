package elke

import spock.lang.Specification

/**
 * Created by SorenHartvig on 16/12/15.
 */
class FjernsynSpec extends Specification {

    Fjernsyn fjernsyn

    void setup() {
        fjernsyn = new Fjernsyn()
    }

    void "test et fantastisk fjernsyn"() {
        given:
        fjernsyn.sortHvid = false
        fjernsyn.hdmi = true
        fjernsyn.taendt = true
        fjernsyn.maerke = 'SONY'

        when:
        boolean resultat = fjernsyn.erMegaFedtFjernsyn()

        then:
        resultat==true
    }

    void "test et dårligt fjernsyn"() {
        given:
        fjernsyn.sortHvid = true
        fjernsyn.hdmi = true
        fjernsyn.taendt = true
        fjernsyn.maerke = "SONY"

        when:
        boolean resultat = fjernsyn.erMegaFedtFjernsyn()

        then:
        resultat==false
    }

    void "test et japansk fjernsyn"() {
        given:
        fjernsyn.maerke = "SONY"

        when:
        boolean resultat = fjernsyn.kommerFraJapan()

        then:
        resultat == true
    }
}
