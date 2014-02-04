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
            } else if (genre.toLowerCase() in ['rock','blues']) {
                theGenre.genreType = GenreType.ROCK
            } else if (genre.toLowerCase() in ['electronica', 'triphop', 'dubstep', 'electronic']) {
                theGenre.genreType = GenreType.ELECTRONIC
            } else if (genre.toLowerCase() in ['pop','funk / soul','funk','soul','reggae','latin','children\'s']) {
                theGenre.genreType = GenreType.POP
            } else if (genre.toLowerCase() in ['hip hop']) {
                theGenre.genreType = GenreType.HIPHOP
            } else if (genre.toLowerCase() in ['metal', 'hardcore', 'industrial', 'hardrock', 'death metal', 'black metal']) {
                theGenre.genreType = GenreType.METAL
            } else if (genre.toLowerCase() in ['folk', 'folk, world & country', 'country', 'folk, world, & country']) {
                theGenre.genreType = GenreType.FOLK_WORLD_COUNTRY
            } else if (genre.toLowerCase() in ['jazz']) {
                theGenre.genreType = GenreType.JAZZ
            } else if (genre.toLowerCase() in ['various']) {
                theGenre.genreType = GenreType.VARIOUS
            } else if (genre.toLowerCase() in ['soundtrack','stage & screen']) {
                theGenre.genreType = GenreType.SOUNDTRACK
            } else if (genre.toLowerCase() in ['classical']) {
                theGenre.genreType = GenreType.CLASSICAL
            } else if (genre.toLowerCase() in ['spoken','spoken word','non-music']) {
                theGenre.genreType = GenreType.SPOKEN_WORD
            } else {
                System.out.println("unknown genre: $genre")

                //throw new DiscogsParserException("unknown Genre : $genre")
            }
        }

        return theGenre
    }
}
