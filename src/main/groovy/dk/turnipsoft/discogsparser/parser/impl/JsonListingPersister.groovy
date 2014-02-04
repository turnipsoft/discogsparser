package dk.turnipsoft.discogsparser.parser.impl

import dk.turnipsoft.discogsparser.api.ListingPersister
import dk.turnipsoft.discogsparser.model.Configuration
import dk.turnipsoft.discogsparser.model.Listing
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.type.TypeReference

/**
 * persists all listings to json
 */
class JsonListingPersister implements ListingPersister {

    Configuration configuration

    @Override
    void init(Configuration configuration) {
        this.configuration = configuration
    }

    File getFile() {
        File file = new File(configuration.generateDirectory+"/"+configuration.databasename)
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    @Override
    void persistListings(List<Listing> listings) {

        ObjectMapper objectMapper = new ObjectMapper()
        File dbFile = getFile()
        objectMapper.writeValue(dbFile, listings)

    }

    @Override
    List<Listing> restoreListings() {
        try {
            ObjectMapper mapper = new ObjectMapper()
            File dbFile = getFile()
            List<Listing> listings = []
            listings = mapper.readValue(dbFile,  new TypeReference<List<Listing>>(){})
            return listings
        } catch(EOFException eof) {
            System.out.println('No Database at this point')
            return []
        }

    }
}
