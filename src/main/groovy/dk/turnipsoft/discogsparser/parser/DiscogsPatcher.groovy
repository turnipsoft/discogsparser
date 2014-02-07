package dk.turnipsoft.discogsparser.parser

import dk.turnipsoft.discogsparser.api.ListingPersister
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Context
import dk.turnipsoft.discogsparser.model.Listing
import dk.turnipsoft.discogsparser.parser.impl.enricher.ReleaseEnricher
import dk.turnipsoft.discogsparser.util.HttpUtil
import groovy.json.JsonSlurper

/**
 * Created by shartvig on 07/02/14.
 */
class DiscogsPatcher {


    class PatchReleaseEnricher extends ReleaseEnricher {

        Map<String, Object> jsonMap
        void setJson(Map<String, Object> jsonMap) {
            this.jsonMap = jsonMap
        }

        @Override
        Map<String, Object> getJSON(String url) {
            return this.jsonMap
        }
    }

    Configuration configuration
    String filename = "patches.txt"
    Context context = new Context()

    JsonSlurper slurper = new JsonSlurper()
    HttpUtil httpUtil = new HttpUtil()

    String readFile(File f) {
        StringBuffer buffer = new StringBuffer()
        BufferedReader br = new BufferedReader(new FileReader(f))
        String line
        while ((line=br.readLine())!=null) {
            buffer.append(line)
        }
        br.close()

        return buffer.toString()
    }

    public void doPatch() {
        this.configuration = new Configuration()
        ListingPersister persister = configuration.persisters.get(0)
        List<Listing> allListings = persister.restoreListings()
        PatchReleaseEnricher releaseEnricher = new PatchReleaseEnricher()
        releaseEnricher.init(configuration)
        context.allListings = allListings

        String dirname = configuration.generateDirectory+"/"+configuration.patchFolder
        File file = new File(dirname)
        File[] files = file.listFiles()
        files.each { patchFile->
            String json = readFile(patchFile)
            Map<String, Object> jsonMap = slurper.parseText(json)
            releaseEnricher.setJson(jsonMap)
            String releaseUrl = jsonMap.get('resource_url')
            List<Listing> listings = findListings(releaseUrl, allListings)
            listings.each { listing->
                System.out.println("trying to enrich lising: $listing.description")
                releaseEnricher.enrich(listing, context)
            }
        }

        persister.persistListings(context.allListings)
    }

    private List<Listing> findListings(String url, List<Listing> allListings) {
        List<Listing> result = []
        allListings.each { listing ->
            if (result) return
            if (listing.releaseUrl == url) {
                result << listing
            }
        }
        return result
    }

    public static void main(String[] args) {
        DiscogsPatcher patcher = new DiscogsPatcher()
        patcher.doPatch()
    }
}
