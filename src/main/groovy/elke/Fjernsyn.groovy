package elke

/**
 * Created by SorenHartvig on 16/12/15.
 */
class Fjernsyn {
    boolean taendt
    boolean sortHvid
    String maerke
    boolean hdmi

    boolean erMegaFedtFjernsyn() {
        return (taendt && !sortHvid && hdmi)
    }

    boolean kommerFraJapan() {
        return (maerke=='SONY' || maerke=='TOSHIBA')
    }

}
