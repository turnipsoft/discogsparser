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
            if (it.buy && it.priceDKK && it.originalPriceDkk) {
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

        println("REPORT ------------- REPORT")
        report(scanResult)
    }

    public void report(List<SimpleReleasePrice> results) {
        println("HIGH HIGH PRIORITY")
        println("_____________")
        report(results,4,30,5000)
        println("")

        println("HIGH PRIORITY")
        println("_____________")
        report(results,4,15,30)
        println("")

        println("PRIORITY")
        println("_____________")
        report(results,4,10,15)
        println("")

        println("MINOR PRIORITY")
        println("_____________")
        report(results,4,10,15)
        println("")

        println("LOW PRIORITY")
        println("_____________")
        report(results,2,5,5000)
        println("")

        println("ALL")
        println("_____________")
        report(results,1,0,5000)
        println("")

    }

    public void report(List<SimpleReleasePrice> results, int minWants, double min, double max) {
        results.each {
            if (it.buy && it.originalPriceDkk && it.priceDKK) {
                int wants = it.want as Integer
                double salgMinusFee = ((it.priceDKK as Double) * 0.9) * 0.97
                double fortjeneste = salgMinusFee - (it.originalPriceDkk as Double)
                if (wants >= minWants && fortjeneste >= min && fortjeneste<max) {
                    println fortjeneste + " - " + it.toString()
                }
            }
        }
    }
}
