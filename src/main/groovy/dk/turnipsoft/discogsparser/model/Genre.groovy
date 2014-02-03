package dk.turnipsoft.discogsparser.model

import dk.turnipsoft.discogsparser.parser.DiscogsParserException

/**
 * Created by shartvig on 03/02/14.
 */
class Genre {

    String originalStyles
    GenreType genreType

    public static Genre getGenreType(Map<String, Object> jsonMap) {

        Genre theGenre = new Genre()
        jsonMap.get('genres').each { genre->
            if (theGenre.genreType!=null) {
                return
            }

            if (genre.toLowerCase() in ['shoegaze', 'alternative', 'indie']) {
                theGenre.genreType = GenreType.ALTERNATIVE
            } else if (genre.toLowerCase() in ['rock']) {
                theGenre.genreType = GenreType.ROCK
            } else if (genre.toLowerCase() in ['electronica', 'triphop', 'dubstep', 'electronic']) {
                theGenre.genreType = GenreType.ELECTRONICA
            } else if (genre.toLowerCase() in ['pop']) {
                theGenre.genreType = GenreType.POP
            } else if (genre.toLowerCase() in ['metal', 'hardcore', 'industrial', 'hardrock', 'death metal', 'black metal']) {
                theGenre.genreType = GenreType.METAL
            } else if (genre.toLowerCase() in ['folk', 'folk, world & country', 'country']) {
                theGenre.genreType = GenreType.FOLK
            } else if (genre.toLowerCase() in ['jazz']) {
                theGenre.genreType = GenreType.JAZZ
            } else if (genre.toLowerCase() in ['various']) {
                theGenre.genreType = GenreType.VARIOUS
            } else if (genre.toLowerCase() in ['soundtrack']) {
                theGenre.genreType = GenreType.SOUNDTRACK
            } else {
                throw new DiscogsParserException("unknown Genre : $genre")
            }
        }

        return theGenre
    }
}
