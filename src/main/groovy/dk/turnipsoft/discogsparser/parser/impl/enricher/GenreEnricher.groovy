package dk.turnipsoft.discogsparser.parser.impl.enricher

import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.GenreType
import dk.turnipsoft.discogsparser.model.Listing

/**
 * Created by shartvig on 06/02/14.
 */
class GenreEnricher implements ListingEnricher {

    Configuration configuration

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    @Override
    void enrich(Listing listing, Context context) {
        if (listing.release.genre.genreType!=GenreType.ALTERNATIVE ||
            listing.release.genre.genreType!=GenreType.METAL || listing.release.genre.genreType!=GenreType.POP) {

            boolean winnerFound = false

            listing.release.jsonMap.get('genres').each { genre->
                if (winnerFound) return
                winnerFound = overrideMainGenre(listing, genre)
            }

            winnerFound = false

            if (listing.release.jsonMap.get('styles')) {
                listing.release.jsonMap.get('styles').each { genre->
                    if (winnerFound) return
                    winnerFound = overrideMainGenre(listing, genre)
                }
            }

        }

        overrideMainHipHop(listing)
        overrideFromProperties(listing)

    }

    public void overrideMainHipHop(Listing listing) {
        listing.release.jsonMap.get('genre').each { genre->
            if (genre.toLowerCase() in ['hiphop', 'hip hop']) {
                listing.release.genre.genreType = GenreType.HIPHOP
            }
        }
    }

    public void overrideFromProperties(Listing listing) {
        GenreType type = configuration.getGenreOverride(listing.release.artistName)
        if (type) {
            listing.release.genre.genreType = type
        }
    }

    public boolean overrideMainGenre(Listing listing, String genre) {
        if (genre.toLowerCase() in ['shoegaze','artrock','art rock','shoegazer','post rock','grunge','indie','new wave', 'indie rock','brit pop', 'alternative','punk','lo-fi','alternative rock']) {
            listing.release.genre.genreType = GenreType.ALTERNATIVE
            return true
        } else if (genre.toLowerCase() in ['electronic','dubstep','chillwave','electro','minimal']) {
            listing.release.genre.genreType = GenreType.ELECTRONIC
        } else if (genre.toLowerCase() in ['metal','heavy metal','speed metal','post metal','trash','thrash','nu metal','black metal','industrial','hardcore','doom metal','grindcore', 'death metal', 'hard rock']) {
            listing.release.genre.genreType = GenreType.METAL
            return true
        }
        return false
    }
}
