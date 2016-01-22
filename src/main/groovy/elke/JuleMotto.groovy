package elke

/**
 * Created by SorenHartvig on 16/12/15.
 */
class JuleMotto {

    String motto

    void setMotto(String m) {
        this.motto = m
    }

    String sigMotto(String navn) {
        return "Hej $navn. $motto"
    }

    //setMotto("Julen har englelyd")
    //this.motto = 'Julen har englelyd'

    //sigMotto("Arved")
    //resultat = Hej Arved. Julen har englelyd
}
