package elke


import spock.lang.Specification
/**
 * Created by SorenHartvig on 17/12/15.
 */
class OstSpec extends Specification{

        Ost ost

    void  setup(){
        ost = new Ost()
     }

    void "test Morgenost er Fantastisk"(){
        given:
        ost.staerk = true
        ost.gul = true

        when:
        boolean resultat = ost.erfantastiskMorgenost()

        then:
        resultat == true


    }
}

