package dk.turnipsoft.discogsparser.parser.impl.enricher

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Genre
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.Medium
import dk.turnipsoft.discogsparser.model.Release
import dk.turnipsoft.discogsparser.model.ReleaseType
import dk.turnipsoft.discogsparser.util.HttpUtil
import groovy.json.JsonException
import groovy.json.JsonSlurper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by shartvig on 03/02/14.
 */
class ReleaseEnricher implements ListingEnricher {

    Configuration configuration
    Logger logger = LoggerFactory.getLogger(ReleaseEnricher.class)

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    Listing findListing(Context context, Listing listing) {
        Listing foundListing = context.allListings.find {it.id==listing.id}
        return foundListing
    }

    public Map<String, Object> getJSON(String url) {
        HttpUtil h = new HttpUtil()
        Thread.sleep(1000)
        String jsonString = h.getJsonWithWget(url, configuration.token)
        JsonSlurper jsonSlurper = new JsonSlurper()
        return jsonSlurper.parseText(jsonString)
    }

    private Medium getMedium(Map<String, Object> jsonMap) {
        Medium medium

        String name = jsonMap.get('formats').get(0).get('name')

        // first pass
        medium = getMedium(name, medium)
        if (!medium && jsonMap.get('formats').size()>1) {
            name = jsonMap.get('formats').get(1).get('name')
            // second pass
            medium = getMedium(name, medium)
        }

        boolean found  = false

        jsonMap.get('formats').get(0).get('descriptions').each{ description->

            if (description.toLowerCase() in ['lp']) {
                medium = new Medium(ReleaseType.VINYLALBUM)

            } else if (description.toLowerCase() in ['album']) {
                return
            } else if (description.toLowerCase() in ['ep','maxi-single','maxi']) {
                if (medium.releaseType == ReleaseType.VINYLALBUM) {
                    medium = new Medium(ReleaseType.VINYLEP)
                } else if (medium.releaseType == ReleaseType.CDALBUM) {
                    medium = new Medium(ReleaseType.CDMAXI)
                }
            } else if (description.toLowerCase() in ['7"']) {
                medium = new Medium(ReleaseType.VINYLSEVEN)
            } else if (description.toLowerCase() in ['single']) {
                if (medium.releaseType == ReleaseType.VINYLALBUM) {
                    medium = new Medium(ReleaseType.VINYLSEVEN)
                } else if (medium.releaseType == ReleaseType.CDALBUM) {
                    medium = new Medium(ReleaseType.CDS)
                }
            } else if (description.toLowerCase() in ['10"']) {
                medium = new Medium(ReleaseType.VINYLTEN)
            } else if (description.toLowerCase() in ['cd']) {
                medium = new Medium(ReleaseType.CDALBUM)
            } else if (description.toLowerCase() in ['cds']) {
                medium = new Medium(ReleaseType.CDS)
            } else if (description.toLowerCase() in ['cd-maxi','cdmaxi']) {
                medium = new Medium(ReleaseType.CDMAXI)
            } else if (description.toLowerCase() in ['cassette']) {
                medium = new Medium(ReleaseType.CASSETTE)
            } else if (description.toLowerCase() in ['dvd', 'dvdr', 'dvd-v']) {
                medium = new Medium(ReleaseType.DVD)
            } else if (description.toLowerCase() in ['bluray']) {
                medium = new Medium(ReleaseType.BLURAY)
            } else {

            }
        }

        if (!medium) {
            logger.warn("can't determine medium: "+jsonMap.get('formats'))
        }

        return medium
    }

    private Medium getMedium(String name, Medium medium) {
// try name
        if (name.toLowerCase() in ['cd', 'cdr']) {
            medium = new Medium(ReleaseType.CDALBUM)
        } else if (name.toLowerCase() in ['vinyl', 'lp']) {
            medium = new Medium(ReleaseType.VINYLALBUM)
        } else if (name.toLowerCase() == 'cassette') {
            medium = new Medium(ReleaseType.CASSETTE)
        } else if (name.toLowerCase() == 'dvd') {
            medium = new Medium(ReleaseType.DVD)
        } else if (name.toLowerCase() == 'bluray') {
            medium = new Medium(ReleaseType.BLURAY)
        }
        medium
    }

    public void enrichListing(Listing listing) {
        try {
            Release release = new Release()

            Map<String, Object> jsonMap = getJSON(listing.releaseUrl)
            release.releaseName = jsonMap.get('title')
            release.year = jsonMap.get('year')
            release.releaseDate = jsonMap.get('released')
            if (!release.releaseDate) {
                release.releaseDate = release.year
            }
            release.artistName = jsonMap.get('artists').get(0).get('name')
            if (release.artistName && release.artistName.endsWith(')')) {
                release.artistName = release.artistName.substring(0,release.artistName.lastIndexOf('(')-1)
            }
            Genre genre = Genre.getGenreType(jsonMap)
            release.genre = genre
            release.country = jsonMap.get('country')
            release.medium = getMedium(jsonMap)
            if (!release.medium) {
                listing.errors.add('Unable to determine medium')
            }

            enrichImages(jsonMap, release)

            release.jsonMap = jsonMap
            listing.release = release


        } catch (JsonException je) {
            logger.error("Unable to enrich: $listing.description ($listing.releaseUrl), due to:$je ")
            listing.errors.add('unable to enrich due to: '+je)
        } catch (Exception e) {
            logger.error("Unable to enrich: $listing.description ($listing.releaseUrl), due to:$e ")
            listing.errors.add('unable to enrich due to: '+e)
        }
    }

    private Object enrichImages(Map<String, Object> jsonMap, Release release) {
        jsonMap.get('images').each { imageMap ->
            if (imageMap.get('type') == 'primary' && imageMap.get('uri150')) {
                String imageUrl = imageMap.get('uri150')
                String imageFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1)
                release.imageFileName = imageFileName
                release.publicImageUrl = configuration.imageBaseUrl + imageFileName
            }
        }

        if (!release.imageFileName && jsonMap.get('images') && jsonMap.get('images').size()>0 ) {
            // just take first
            logger.debug("using secondary image")
            String uri150 = jsonMap.get('images').get(0).get('uri150')
            if (uri150) {
                String imageFileName = uri150.substring(uri150.lastIndexOf("/") + 1)
                release.imageFileName = imageFileName
                release.publicImageUrl = configuration.imageBaseUrl + imageFileName
            }

        }
    }

    @Override
    void enrich(Listing listing, Context context) {
        Listing existingListing = findListing(context, listing)
        if (!existingListing || !existingListing.release || !existingListing.release.releaseName || !existingListing.release.medium || !existingListing.release.imageFileName) {
            //enrich release
            logger.debug("Enriching : $listing.description")
            enrichListing(listing)
        } else {
            existingListing.setPriceEur(listing.priceEur)
            listing.setRelease(existingListing.release)
            if (!listing.release.imageFileName) {
                logger.debug("enriching images on : $listing.description")
                Map<String, Object> jsonMap = listing.release.jsonMap
                enrichImages(jsonMap, listing.release)
            }
        }
    }
}
