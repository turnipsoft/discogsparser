package dk.turnipsoft.discogsparser.pricer

import dk.turnipsoft.discogsparser.model.SimpleListingPrice

/**
 * Created by shartvig on 22/01/16.
 */
class SimpleFileParser {

    String fileName
    public SimpleFileParser(String fileName) {
        this.fileName = fileName
    }

    public List<SimpleListingPrice> readFile() {
        BufferedReader br = new BufferedReader(new FileReader(fileName))

        List<SimpleListingPrice> slpList = []
        String mediaType = br.readLine()
        String oPrice = ""
        if (mediaType.contains("-")) {
            oPrice = mediaType.substring(mediaType.indexOf("-")+1)
            mediaType = mediaType.substring(0, mediaType.indexOf("-"))
        }
        String s = null
        while ((s=br.readLine())!=null) {
            if (s.toLowerCase().contains('solgt')) {
                println "Skipping $s"
                continue;
            }
            s = s.replace("–","-").replace(":","-").trim()
            SimpleListingPrice slp = new SimpleListingPrice()
            slp.originalSalesLine = s

            if (s.contains(",-")) {
                s = s.substring(0, s.indexOf(',-')+2)
            }
            if (s.endsWith('kr') || s.endsWith(',-')) {
                s = s.substring(0, s.length()-2)
            } else if (s.endsWith('kr.')) {
                s = s.substring(0, s.length()-3)
            }
            s=s.trim()
            int potentialOriginalPriceIdx = s.lastIndexOf(" ")
            String potentialOriginalPrice = s.substring(potentialOriginalPriceIdx+1)

            if (potentialOriginalPrice.isNumber()) {
                slp.priceDkk = potentialOriginalPrice
                s = s.substring(0,potentialOriginalPriceIdx)
            }
            String[] splitted = s.split("-")
            slp.artistName = cleanRelease(splitted[0].trim())

            if (splitted.length==2) {
                slp.releaseName = cleanRelease(splitted[1].trim())
            }

            slp.mediatype = mediaType
            if (!slp.priceDkk) {
                slp.priceDkk = oPrice
            }
            slpList << slp
        }

        return slpList
    }

    String cleanRelease(String release) {
        return release.replace(" EP","").replace(".","").replace("digi","").replace("DIGI","").replace("2CD","")
    }
}
