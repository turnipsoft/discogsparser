package dk.turnipsoft.discogsparser.model

/**
 * Created by shartvig on 03/02/14.
 */
class Release {
    String releaseName
    Map<String, Object> jsonMap
    String releaseUrl
    Genre genre
    Artist artist
    String artistName
    Medium medium
    int year
    String releaseDate
    String country
    String imageFileName
    String publicImageUrl
}
