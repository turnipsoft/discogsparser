package dk.turnipsoft.discogsparser.pricer

import dk.turnipsoft.discogsparser.model.PricerConfiguration
import dk.turnipsoft.discogsparser.model.SimpleReleasePrice
import dk.turnipsoft.discogsparser.util.HttpUtil

/**
 * Created by shartvig on 22/01/16.
 */
class PriceScanner {

    String SEARCH_URL = 'http://www.discogs.com/search/?q='
    String BASE_URL = 'http://www.discogs.com'
    String SALES_URL = 'http://www.discogs.com/sell/list?sort=price%2Casc&limit=250&master_id=${masterId}&ev=mb'
    String SALES_URL_SINGLE = 'http://www.discogs.com/sell/release/'

    String mediatype
    HttpUtil httpUtil
    PricerConfiguration pricerConfiguration

    public PriceScanner(String mediaType) {
        this.mediatype = mediaType
        httpUtil = new HttpUtil()
        pricerConfiguration = new PricerConfiguration()
    }

    String transformStringToSearchString(String s, String type) {
        String search = s.toLowerCase().replace(' ','+')
        return "${search}&type=${type}"
    }

    SimpleReleasePrice performScan(String artistname, String releasename) {
        String search = artistname + ' ' + (releasename?:'')
        search = search.trim()
        SimpleReleasePrice simpleReleasePrice = new SimpleReleasePrice()
        simpleReleasePrice.artistName = artistname
        simpleReleasePrice.release = releasename

        byte [] result = httpUtil.getBytesFromUrl(SEARCH_URL+transformStringToSearchString(search, 'master'))
        String htmlResult = new String(result, 'UTF-8')

        String masterUrl = getMasterUrl(htmlResult,search)
        if (masterUrl) {
            masterUrl = BASE_URL + masterUrl
            String masterId = getMasterId(masterUrl)

            String salesUrl = SALES_URL.replace('${masterId}', masterId)

            result = httpUtil.getBytesFromUrl(salesUrl)
            htmlResult = new String(result, 'UTF-8')
            parseAndEnrich(htmlResult, simpleReleasePrice)
        } else {
            performSingleScan(search, simpleReleasePrice)
        }

        return simpleReleasePrice
    }

    SimpleReleasePrice performSingleScan(String search, SimpleReleasePrice srp) {
        byte [] result = httpUtil.getBytesFromUrl(SEARCH_URL+transformStringToSearchString(search,'release'))
        String htmlResult = new String(result, 'UTF-8')

        String url = getMasterUrl(htmlResult, search)
        if (!url) {
            srp.noData = true
            return srp
        }
        String itemId = getMasterId(url)

        result = httpUtil.getBytesFromUrl(SALES_URL_SINGLE+itemId)
        htmlResult = new String(result, 'UTF-8')
        parseAndEnrich(htmlResult, srp)
        return srp
    }

    static double EUR = 7.5
    static double USD = 7
    static double GBP = 10
    static double AUD = 5

    String getPriceDkk(String price) {
        price = price.trim()
        String valuta = price.substring(0,1)

        double priceFactor=0
        double priceDouble = 0
        if (valuta == '$') {
            priceFactor = USD
            priceDouble = price.substring(1) as Double
        } else if (valuta == '€') {
            priceFactor = EUR
            priceDouble = price.substring(1) as Double
        } else if (valuta == '£') {
            priceFactor = GBP
            priceDouble = price.substring(1) as Double
        }

        if (priceFactor==0) {
            if (price.startsWith('A$')) {
                priceFactor = AUD
                priceDouble = price.substring(2) as Double
            }
        }

        return priceFactor * priceDouble as String
    }


    SimpleReleasePrice parseAndEnrich(String html, SimpleReleasePrice srp) {

        boolean foundMatch = false
        while (!foundMatch) {
            int idxTrStart = html.indexOf("<tr class=\"shortcut_navigable \">")
            if (idxTrStart<0) {
                println("Didn't find any")
                srp.noData = true
                return srp
            }
            String rest = html.substring(idxTrStart)
            int idxTrEnd = rest.indexOf("</tr>")
            String firstSell = rest.substring(0, idxTrEnd)

            srp.price = getClassValue('price', firstSell)
            srp.priceDKK = getPriceDkk(srp.price)
            srp.catalogNO = getClassValue('item_catno', firstSell)
            srp.conditionMedia = getClassValue('item_media_condition', firstSell)
            srp.conditionSleeve = getClassValue('item_sleeve_condition', firstSell)
            srp.itemDescriptionTitle = getClassValue('item_description_title', firstSell)
            srp.mediatype = mediatype
            srp.want = getWant(firstSell)
            if (isCandidate(srp)) {
                return srp
            }
            html = html.substring(idxTrEnd+5)
        }
    }

    boolean isCandidate(SimpleReleasePrice srp) {
        if (srp.itemDescriptionTitle.toLowerCase().contains("promo") || srp.itemDescriptionTitle.toLowerCase().contains("unofficial")) {
            return false
        }

        if (! (srp.itemDescriptionTitle.toLowerCase().contains("(${srp.mediatype.toLowerCase()}") ||
               srp.itemDescriptionTitle.toLowerCase().contains("(2x${srp.mediatype.toLowerCase()}") )) {
            if (srp.mediatype.toLowerCase()=='lp') {
                if (srp.itemDescriptionTitle.contains("(12")) {
                    // intentionally blank.. 12" kan godt gåi kategori LP, da det bare er vinyler man leder efter
                } else {
                    return false
                }
            }

        }

        boolean gradingMediaOk = false
        boolean gradingSleeveOk = false
        pricerConfiguration.allowedGradings.each {
            if (srp.conditionMedia && srp.conditionMedia.contains(it)) {
                gradingMediaOk = true
            }

            if (srp.conditionSleeve && srp.conditionSleeve.contains(it)) {
                gradingSleeveOk = true
            }
        }

        return gradingMediaOk && gradingSleeveOk

    }

    String getWant(String html) {
        int idx = html.indexOf("want_indicator")
        if (idx<0) {
            return "0"
        }
        String s = html.substring(idx)
        return getClassValue('community_number', s)
    }

    String getClassValue(String className, String html) {
        int idx = html.indexOf("class=\"${className}\"")
        if (idx<0) {
            return 'NO DATA'
        }
        String rest = html.substring(idx+(9+className.length()))
        int idxEnd = rest.indexOf("<")
        String result = rest.substring(0,idxEnd)
        idx = result.indexOf(">")
        if (idx>0) {
            result = result.substring(idx+1)
        }
        return result.trim()
    }

    String cleanSearch(String search) {
        return search.replace("'",'').replace('"','').replace('_','').replace('/','').replace('“','').replace('”','')
    }

    String getMasterUrl(String html, String search) {
        search = cleanSearch(search)
        if (search.toLowerCase().startsWith("the")) {
            search = search.replace('The ','').replace('the ', '').replace('THE ','')
        }
        String masterMatch = '/'+search.toLowerCase().replace(' ','-')
        int idx = html.toLowerCase().indexOf(masterMatch)
        if (idx<0) {
            return null
        }
        String rest = html.substring(idx)
        idx = rest.indexOf('"')
        String url = rest.substring(0,idx)

        return url
    }

    String getMasterId(String masterUrl) {
        int idx = masterUrl.lastIndexOf("/")
        return masterUrl.substring(idx+1)
    }

}
