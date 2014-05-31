package dk.turnipsoft.discogsparser.parser.impl.processor

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.GenreType
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.model.MasterReleaseType
import dk.turnipsoft.discogsparser.util.FileUtil

/**
 * Created by shartvig on 02/05/14.
 */
class GenreArtistListProcessor implements ListingProcessor {

    Configuration configuration
    Map<String, StringBuffer> genreArtists = [:]

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    private StringBuffer getGenreArtistBuffer(GenreType genreType, MasterReleaseType mrt) {
        String key = genreType.name() + "_" + mrt.name()
        if (genreArtists.containsKey(key)) {
            return genreArtists.get(key)
        } else {
            StringBuffer buffer = new StringBuffer()
            genreArtists.put(key, buffer)
            return buffer;
        }
    }

    @Override
    Object processListing(Listing listing) {

        try {
            StringBuffer buffer = getGenreArtistBuffer(listing.release.genre.genreType, listing.release.medium.masterReleaseType)
            if (!buffer.contains(listing.release.artistName) && listing.release.artistName!='Various') {
                buffer.append(", "+listing.release.artistName+ "\n")
            }
        } catch(Exception e) {
            System.out.println("TROUBLES WITH: "+listing.description+", "+listing.releaseUrl)
        }
    }

    @Override
    void endProcessing(Context context) {
        for (String key: genreArtists.keySet()) {
            System.out.println("Writing Genre Artist $key")
            StringBuffer buf = genreArtists.get(key)
            String fileName = configuration.generateDirectory + "/" +key +"_artist_sales.txt"
            FileUtil.writeFile(fileName, buf)
        }

    }
}
