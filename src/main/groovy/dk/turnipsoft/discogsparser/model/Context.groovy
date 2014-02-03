package dk.turnipsoft.discogsparser.model

/**
 * Created by shartvig on 03/02/14.
 */
class Context {

    List<Listing> allListings = []

    void initContext(Configuration configuration) {
        String dbfilename = configuration.generateDirectory + '/' + configuration.databasename
        File f = new File(dbfilename)
        if (f.exists()) {
            //loadfile with JsonSlurper
        }
    }
}
