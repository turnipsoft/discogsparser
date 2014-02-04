package dk.turnipsoft.discogsparser.model

import dk.turnipsoft.discogsparser.api.DiscogsSource
import dk.turnipsoft.discogsparser.api.ListingEnricher
import dk.turnipsoft.discogsparser.api.ListingPersister
import dk.turnipsoft.discogsparser.api.ListingProcessor
import dk.turnipsoft.discogsparser.util.ClasspathLoader
import groovy.json.JsonSlurper

/**
 * Created by shartvig on 03/02/14.
 */
class Configuration {

    String baseurl
    String generateDirectory
    String databasename
    String username
    String listingurl
    String releaseurl
    String listingDirectory
    String overrideGenreFile
    String imageBaseUrl
    String turnipImageBaseUrl
    List<ListingEnricher> enrichers
    List<ListingProcessor> processors
    List<ListingPersister> persisters
    Map<String,GenreType> genreOverride = [:]
    DiscogsSource source

    public Configuration() {
        this('configuration.json')
    }

    public Configuration(String filename) {
        ClasspathLoader classpathLoader = new ClasspathLoader()
        JsonSlurper jsonSlurper = new JsonSlurper()
        InputStream jsonInputStream = classpathLoader.getInputStream(filename)
        Map jsonMap = jsonSlurper.parseText(jsonInputStream.getText('UTF-8'))
        Map configMap = jsonMap.get('configuration')

        this.generateDirectory = configMap.get('generatedir')
        this.databasename = configMap.get('databasename')
        this.listingurl = configMap.get('listingurl')
        this.releaseurl = configMap.get('releaseurl')
        this.username = configMap.get('username')
        this.listingDirectory = configMap.get('listingDirectory')
        this.imageBaseUrl = configMap.get('imageBaseUrl')
        this.turnipImageBaseUrl = configMap.get('turnipImageBaseUrl')

        List<String> list = configMap.get('enrichers')
        this.enrichers = []
        list.each { enricherClass ->
            Class handlerClazz = Class.forName(enricherClass)
            ListingEnricher enricher = handlerClazz.newInstance()
            enricher.init(this)
            this.enrichers << enricher
        }

        list = configMap.get('processors')
        this.processors = []
        list.each { processorClass ->
            Class handlerClazz = Class.forName(processorClass)
            ListingProcessor processor = handlerClazz.newInstance()
            processor.init(this)
            this.processors << processor
        }

        list = configMap.get('persisters')
        this.persisters = []
        list.each { persisterClass ->
            Class handlerClazz = Class.forName(persisterClass)
            ListingPersister persister = handlerClazz.newInstance()
            persister.init(this)
            this.persisters << persister
        }

        String sourceClass = configMap.get('source')
        Class sourceClazz = Class.forName(sourceClass)
        this.source = sourceClazz.newInstance()
        this.source.init(this)


        loadGenres()
    }

    private Properties loadProperties(String filename) {
        ClasspathLoader classpathLoader = new ClasspathLoader()
        InputStream is = classpathLoader.getInputStream(filename)
        Properties properties = new Properties()
        properties.load(is)
        return properties
    }

    private void loadGenres() {
        Properties properties = loadProperties('genre.properties')
        properties.propertyNames().each { name ->
            String value = properties.getProperty(name)
            name = name.replace('_',' ').replace('.','').replace(',','').replace('-','').replace('\'','')
            GenreType genreType = GenreType.valueOf(value)
            genreOverride.put(name, genreType)
        }
    }

}
