package elke

import spock.lang.Specification

/**
 * Created by SorenHartvig on 16/12/15.
 */
class JuleMottoSpec extends Specification {
    JuleMotto juleMotto

    void setup() {
        juleMotto = new JuleMotto()
    }

    void "test set julemotto"() {
        when:
        juleMotto.setMotto('Julen har englelyd')

        then:
        juleMotto.motto == 'Julen har englelyd'
    }

    void "test sig Arved"() {
        when:
        juleMotto.setMotto('Æggesnaps forever')
        String resultat = juleMotto.sigMotto("Arved")

        then:
        resultat == 'Hej Arved. Æggesnaps forever'
    }
}
