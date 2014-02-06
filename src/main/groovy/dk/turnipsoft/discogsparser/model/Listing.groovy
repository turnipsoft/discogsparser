package dk.turnipsoft.discogsparser.model

import org.codehaus.jackson.annotate.JsonIgnoreProperties

/**
 * Created by shartvig on 03/02/14.
 */

@JsonIgnoreProperties(['imageFileName','publicImageUrl'])
class Listing {

    String description
    double priceEur
    double priceDkk
    String comment
    Release release
    Grading sleeveGrading
    String sleeveGradingString
    Grading discGrading
    String discGradingString
    boolean forSale
    String releaseUrl
    long id
    boolean processed
    String catalogNo


    List<String> errors = []

    String listingJson

    boolean isForSale() {
        return forSale
    }

    boolean isProcessed() {
        return processed
    }

}
