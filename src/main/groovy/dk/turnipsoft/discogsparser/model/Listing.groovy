package dk.turnipsoft.discogsparser.model

/**
 * Created by shartvig on 03/02/14.
 */
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

    String listingJson

}
