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
            srp.originalSalesLine = it.originalSalesLine
            println srp.toString() + "-------- ${it.originalSalesLine}"
            if (srp.hentFortjeneste()>0) {
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

        println("-------------- REPORT -------------")
        report(scanResult)
    }

    public void report(List<SimpleReleasePrice> results) {
        println("")
        println("MAYBE CHECK THESE COULDN'T FIND DATA")
        println("_____________")

        results.findAll{it.noData}.each {
            println it.originalSalesLine
        }

        reportOrdre("MUST", results, 4, 100,10000)
        reportOrdre("HIGH", results, 4, 30,100)
        reportOrdre("MEDIUM", results, 4, 20,30)
        reportOrdre("MEDIUM-LOW", results, 4, 10,20)
        reportOrdre("LOW", results, 4, 0,10)
        reportOrdre("ALL", results, 2, 0,10000)

    }



    double hentFortjeneste(SimpleReleasePrice srp) {
        double salgMinusFee = ((srp.priceDKK as Double) * 0.9) * 0.97
        double fortjeneste = salgMinusFee - (srp.originalPriceDkk as Double)
        return fortjeneste
    }

    public void reportOrdre(String prio, List<SimpleReleasePrice> results, int minWants, double min, double max) {
        println "############################################################"
        println "-------- $prio ($minWants , $min - $max) ---------"
        println ""
        double sum = 0
        double original = 0
        double salgssum = 0
        results.each {
            if (it.buy && it.originalPriceDkk && it.priceDKK) {
                int wants = it.want as Integer
                double fortjeneste = hentFortjeneste(it)
                if (wants >= minWants && fortjeneste >= min && fortjeneste<max) {
                    sum+=fortjeneste
                    original += (it.originalPriceDkk as Double)
                    salgssum += (it.priceDKK as Double)
                    println it.originalSalesLine
                }
            }
        }

        println("")
        println("----BI----")
        println("")
        results.each {
            if (it.buy && it.originalPriceDkk && it.priceDKK) {
                int wants = it.want as Integer
                double fortjeneste = hentFortjeneste(it)
                if (wants >= minWants && fortjeneste >= min && fortjeneste<max) {
                    println "Fortjeneste ${fortjeneste}, Wants: ${wants}, Min.price: ${it.priceDKK} : ${it.itemDescriptionTitle} , ${it.catalogNO}"
                }
            }
        }
        println("")
        println("Samlet fortjeneste: $sum")
        println("Købspris: ${original}")
        println("Salgspris: ${salgssum}")
        println ""
        println "############################################################"
        println ""

    }

}
