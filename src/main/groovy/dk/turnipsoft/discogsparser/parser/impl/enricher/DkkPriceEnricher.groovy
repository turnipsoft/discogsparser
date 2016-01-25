package dk.turnipsoft.discogsparser.parser.impl.enricher

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 04/02/14.
 */
class DkkPriceEnricher implements ListingEnricher {

    Configuration configuration

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    double dkk = 7.5
    double factor = 0.90

    @Override
    void enrich(Listing listing, Context context) {
        int dkkPrice = listing.getPriceEur() * dkk * factor
        if (dkkPrice<10) {
            dkkPrice = 5
        } else if (dkkPrice<15) {
            dkkPrice=10
        } else if (dkkPrice<20) {
            dkkPrice=15
        } else if (dkkPrice>=20 && dkkPrice<30) {
            dkkPrice = 25
        } else if (dkkPrice>=30 && dkkPrice<35) {
            dkkPrice = 30
        } else if (dkkPrice>=35 && dkkPrice<45) {
            dkkPrice = 40
        } else if (dkkPrice>=45 && dkkPrice<60) {
            dkkPrice = 50
        } else if (dkkPrice>=200 && dkkPrice<500) {
            dkkPrice = roundUp(dkkPrice, 25)
        } else if (dkkPrice>=500) {
            dkkPrice = roundUp(dkkPrice, 50)
        } else {
            dkkPrice = roundUp(dkkPrice, 10)
        }

        listing.setPriceDkk(dkkPrice)
    }

    int roundUp(int num, double nearest) {
        return (int) (Math.ceil(num / nearest) * nearest);
    }
}
