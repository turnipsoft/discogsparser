package dk.turnipsoft.discogsparser.pricer

import dk.turnipsoft.discogsparser.model.PricerConfiguration
import dk.turnipsoft.discogsparser.model.SimpleListingPrice
import dk.turnipsoft.discogsparser.model.SimpleReleasePrice

/**
 * Created by shartvig on 22/01/16.
 */
class DiscogsPricer {

    PricerConfiguration pricerConfiguration

    public static void main(String []args) {
        DiscogsPricer pricer = new DiscogsPricer()
        pricer.pricerConfiguration = new PricerConfiguration()
        pricer.runPricer()
    }

    public void runPricer() {
        SimpleFileParser sfp  = new SimpleFileParser(pricerConfiguration.inputFile)
        List<SimpleListingPrice> releasesToScan = sfp.readFile()
        List<SimpleReleasePrice> scanResult = []

        releasesToScan.each {
            PriceScanner scanner = new PriceScanner(it.mediatype)
            SimpleReleasePrice srp = scanner.performScan(it.artistName,it.releaseName)
            srp.originalPriceDkk = it.priceDkk
            println srp
            if (srp.originalPriceDkk<srp.priceDKK) {
                srp.buy = true
            }
            scanResult << srp
        }

        println("")
        println("Køb dem her evt.")
        scanResult.each {
            if (it.buy) {
                double f = (it.priceDKK as double) - (it.originalPriceDkk as double)
                String b = ""
                if (f>pricerConfiguration.threshold) {
                    b+="! "
                }
                if (f>pricerConfiguration.bonus) {
                    b+="!!!!! "
                }
                println "$b-Fortjeneste: ${f} - ${it.toString()}"
            }
        }
    }
}
