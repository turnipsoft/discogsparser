package dk.turnipsoft.discogsparser.model

/**
 * Created by shartvig on 22/01/16.
 */
class SimpleReleasePrice {
    String artistName
    String release
    String mediatype
    String price
    String priceDKK
    String conditionSleeve
    String conditionMedia
    String catalogNO
    String originalPriceDkk
    String itemDescriptionTitle
    String originalSalesLine
    boolean noData = false
    boolean buy
    String want
    String have

    public String toString() {
        double f = hentFortjeneste()

        return noData ? "$artistName - $release : Unable to obtain data" :
        "$itemDescriptionTitle ($mediatype), Cat:${catalogNO}, condition: ${conditionMedia}/${conditionSleeve}, want: ${want}, " +
                "price:  $price / $priceDKK, originalPrice: $originalPriceDkk ${f>0?', BUY!':''}"
    }

    double hentFortjeneste() {
        if (this.originalPriceDkk && this.priceDKK) {
            double salgMinusFee = ((this.priceDKK as Double) * 0.9) * 0.97
            double fortjeneste = salgMinusFee - (this.originalPriceDkk as Double)
            return fortjeneste
        } else {
            return 0
        }
    }

    public String toHtmlTableRow() {
        return "<tr>" +
                "<td>$artistName</td>" +
                "<td>$release</td>" +
                "<td>$mediatype</td>" +
                "<td>$catalogNO</td>" +
                "<td>$conditionMedia</td>" +
                "<td>$conditionSleeve</td>" +
                "<td>$price</td>" +
                "<td>$priceDKK</td>" +
                "<td>$originalPriceDkk</td>" +
                "</tr>"
    }
}
