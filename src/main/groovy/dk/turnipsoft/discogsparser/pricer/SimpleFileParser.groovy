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
            s = s.replace("–","-")
            SimpleListingPrice slp = new SimpleListingPrice()
            int potentialOriginalPriceIdx = s.lastIndexOf(" ")
            String potentialOriginalPrice = s.substring(potentialOriginalPriceIdx+1)
            if (potentialOriginalPrice.toLowerCase().endsWith("kr") || potentialOriginalPrice.endsWith(",-")) {
                potentialOriginalPrice = potentialOriginalPrice.substring(0, potentialOriginalPrice.length()-2)
            }
            if (potentialOriginalPrice.isNumber()) {
                slp.priceDkk = potentialOriginalPrice
                s = s.substring(0,potentialOriginalPriceIdx)
            }
            String[] splitted = s.split("-")
            slp.artistName = splitted[0].trim()
            slp.releaseName = cleanRelease(splitted[1].trim())
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
