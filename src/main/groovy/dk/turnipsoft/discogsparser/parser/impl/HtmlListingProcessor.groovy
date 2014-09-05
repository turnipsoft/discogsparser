package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Genre
import dk.turnipsoft.discogsparser.model.GenreType
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.util.FileUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by shartvig on 03/02/14.
 */
class HtmlListingProcessor implements ListingProcessor {

    Configuration configuration
    Logger logger = LoggerFactory.getLogger(HtmlListingProcessor.class)
    int counter = 0
    boolean white = false

    class HtmlListing implements Comparable<HtmlListing> {
        Listing listing
        String listingHtml

        @Override
        int compareTo(HtmlListing htmlListing) {
            return listing.description.compareTo(htmlListing.listing.description)
        }
    }

    String buildHeading(String name, String href) {
        return "<a name='$href'><h1>$name</h1><br/>"
    }

    String buildListingRow(Listing listing, int index, boolean isWhite) {
        String color = isWhite ? "ffffff" : "c7e1cf"
        int price = listing.priceDkk
        String desc = cutDescription(listing.description)
        String imageUrl = ''
        if (listing.release.imageFileName)
            imageUrl = (listing.release.imageFileName.endsWith('jpeg') || listing.release.imageFileName.endsWith(('jpg'))) ? "http://www.turnips.dk/images/jpeg-small/$listing.release.imageFileName" : "http://www.turnips.dk/images/$listing.release.imageFileName"
        //return "<tr id='r$index' onmouseover=\"fnLoadImage('http://www.turnips.dk/images/$listing.release.imageFileName',event)\" bgcolor='$color'><td align='left' valign='top'><input type=\"checkbox\" name=\"c$index\" id=\"c$index\" /></td><td align='left' valign='top'>$listing.release.artistName</td><td align='left' valign='top'>$desc<br/><br/>$listing.catalogNo, $listing.release.country $listing.release.releaseDate&nbsp;<br/><br/>$listing.discGradingString / $listing.sleeveGradingString<br/><br/><font color='990000'>$listing.comment</font></td><td align='left' valign='top'>$price,-</td></tr>"
        return "<tr id='r$index' bgcolor='$color'><td align='left' valign='top'><input type=\"checkbox\" name=\"c$index\" id=\"c$index\" /></td><td align='left' valign='top'><img border='no' src='$imageUrl' /></td><td align='left' valign='top'>$listing.release.artistName</td><td align='left' valign='top'>$desc<br/><br/>$listing.catalogNo, $listing.release.country $listing.release.releaseDate&nbsp;<br/><br/>$listing.discGradingString / $listing.sleeveGradingString<br/><br/><font color='990000'>$listing.comment</font></td><td align='left' valign='top'><div id='price$index'>$price,-</div></td></tr>"
    }

    String cutDescription(String description) {
        String s = description.substring(description.indexOf("- ")+2)
        if (s.length() > 150) {
            s = s.substring(0,149)+"..."
        }
        return s
    }

    String buildCDHtml() {

    }

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    Map<GenreType, List<HtmlListing>> cdHtml = [:]
    Map<GenreType, List<HtmlListing>> movieHtml = [:]
    Map<GenreType, List<HtmlListing>> cassetteHtml = [:]
    Map<GenreType, List<HtmlListing>> vinylHtml = [:]

    @Override
    Object processListing(Listing listing) {
        if (listing.release && listing.release.medium && listing.release.releaseName) {
            HtmlListing htmlListing = new HtmlListing( ['listing':listing, 'listingHtml':''])
            if (listing.release.medium.CD) {
                addHtml(htmlListing, cdHtml)
            } else if (listing.release.medium.vinyl) {
                addHtml(htmlListing, vinylHtml)
            } else if (listing.release.medium.cassette) {
                addHtml(htmlListing, cassetteHtml)
            } else if (listing.release.medium.movie) {
                addHtml(htmlListing, movieHtml)
            }
            listing.processed = true
        } else {
            logger.warn("Unable to process: $listing.description")
            listing.errors.add('No release Information')
        }
    }

    void addHtml(HtmlListing htmlListing, Map<GenreType,List<HtmlListing>> map) {
        List<HtmlListing> list = map.get(htmlListing.listing.release.genre.genreType)
        if (!list) {
            list = []
            map.put(htmlListing.listing.release.genre.genreType, list)
        }
        list.add(htmlListing)
    }

    @Override
    void endProcessing(Context context) {
        System.out.println('Doing all the processing end magic')
        sortAll()
        generateHtmlPages()
        context.htmlListingProcessor = this
    }

    private String toHtml(Listing listing) {
        return ''
    }

    private void generateHtmlPages() {
        writeFile('cds_for_sale.html', generateHtmlPage('CD',cdHtml))
        writeFile('vinyls_for_sale.html', generateHtmlPage('Vinyl',vinylHtml))
        writeFile('cassettes_for_sale.html', generateHtmlPage('Cassette',cassetteHtml))
        writeFile('movies_for_sale.html', generateHtmlPage('DVD',movieHtml))
        writeFile('metal_cds_for_sale.html', generateMetalHtmlPage('CD',cdHtml))
        writeFile('metal_vinyls_for_sale.html', generateMetalHtmlPage('Vinyl',vinylHtml))
    }

    private void writeFile(String filename, List<String> list) {
        filename = configuration.generateDirectory+"/"+filename
        FileUtil.writeFile(filename, list)
    }

    private List<String> generateHtmlPage(String media, Map<GenreType, List<HtmlListing>> map) {
        List<String> result = []
        counter = 0
        white = false

        String bool = media.equals("Vinyl") ? 'true' : 'false'

        String startPage = "<form name=\"records\">" +
                "<br/><input type=\"button\" name=\"collect\" value=\"Saml liste over plader\" onclick=\"javascript:collectRecords("+bool+")\"/>\n" +
                "<br/><h1>$media</h1>\n" +
                "<br/>"

        result.add(startPage)
        result.addAll(generateGenre("indie",media,"Indie/Alternative",map.get(GenreType.ALTERNATIVE)))
        result.addAll(generateGenre("electronic",media,"Elektronisk",map.get(GenreType.ELECTRONIC)))
        result.addAll(generateGenre("metal",media,"Metal/Industrial/Hardcore",map.get(GenreType.METAL)))
        result.addAll(generateGenre("rock",media,"Rock",map.get(GenreType.ROCK)))
        result.addAll(generateGenre("pop",media,"Pop/Reggae/Funk/Soul/Latino",map.get(GenreType.POP)))
        result.addAll(generateGenre("hiphop",media,"Hip Hop",map.get(GenreType.HIPHOP)))
        result.addAll(generateGenre("folk_world_country",media,"Folk/World/Country",map.get(GenreType.FOLK_WORLD_COUNTRY)))
        result.addAll(generateGenre("soundtrack",media,"Soundtrack",map.get(GenreType.SOUNDTRACK)))
        result.addAll(generateGenre("various",media,"Opsamlinger",map.get(GenreType.VARIOUS)))
        result.addAll(generateGenre("comedy",media,"Underholdning",map.get(GenreType.COMEDY)))
        result.addAll(generateGenre("klassisk",media,"Opsamlinger",map.get(GenreType.CLASSICAL)))
        result.addAll(generateGenre("spoken",media,"Andet",map.get(GenreType.SPOKEN_WORD)))

        String endPage="<br/>\n" +
                "<br/>\n" +
                "<br/><input type=\"button\" name=\"collect\" value=\"Saml liste over plader\" onclick=\"javascript:collectRecords("+bool+")\"/></form><script language='javascript'>maxRows=$counter</script>"

        result.add(endPage)
        return result

    }

    private List<String> generateMetalHtmlPage(String media, Map<GenreType, List<HtmlListing>> map) {
        List<String> result = []
        counter = 0
        white = false

        String bool = media.equals("Vinyl") ? 'true' : 'false'

        String startPage = "<form name=\"records\">" +
                "<br/><input type=\"button\" name=\"collect\" value=\"Saml liste over plader\" onclick=\"javascript:collectRecords("+bool+")\"/>\n" +
                "<br/><h1>$media</h1>\n" +
                "<br/>"

        result.add(startPage)
        result.addAll(generateGenre("metal",media,"Metal/Industrial/Hardcore",map.get(GenreType.METAL)))

        String endPage="<br/>\n" +
                "<br/>\n" +
                "<br/><input type=\"button\" name=\"collect\" value=\"Saml liste over plader\" onclick=\"javascript:collectRecords("+bool+")\"/></form><script language='javascript'>maxRows=$counter</script>"

        result.add(endPage)
        return result

    }

    private List<String> generateGenre(String href, String prefix, String genre, List<HtmlListing> list) {
        List<String> result = []

        String div = "<div id='$prefix$href'>"
        String divEnd = "</div>"

        String intro = "<a name='$prefix$href'><h2>$genre</h2></a><br/>"
        String table = '''<table border='0'>
                            <tr><td><strong>-</strong></td><td>&nbsp;</td><td><strong>Kunstner</strong></td>
                            <td><strong>Titel/Katalog/Grading</strong></td><td><strong>Pris</strong></td></tr>'''
        String tableEnd = '''</table>'''
        result.add(div)
        result.add(intro)
        result.add(table)

        list.each { listing->
            if (listing.listing.release) {
                listing.listingHtml = buildListingRow(listing.listing, counter++, white)
                white = !white
                result.add(listing.listingHtml)
            } else {
                logger.debug("Unable to generate HTML for $listing.listing.description, no release info")
            }
        }

        result.add(tableEnd)
        result.add(divEnd)
        return result
    }

    private void sortAll() {
        sortMap(cdHtml)
        sortMap(vinylHtml)
        sortMap(cassetteHtml)
        sortMap(movieHtml)
    }

    private void sortMap(Map<GenreType, List<HtmlListing>> map) {
        map.each {entry->
            List<HtmlListing> listings = entry.value
            entry.value = listings.sort()
        }
    }
}
