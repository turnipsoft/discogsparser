package dk.turnipsoft.discogsparser.model

import dk.turnipsoft.discogsparser.util.ClasspathLoader
import groovy.json.JsonSlurper

/**
 * Created by shartvig on 22/01/16.
 */
class PricerConfiguration {

    String inputFile
    String outputDir
    String[] allowedGradings
    String wantThreshold
    double threshold
    double bonus

    public PricerConfiguration() {
        this('pricerconfiguration.json')
    }

    public PricerConfiguration(String filename) {
        ClasspathLoader classpathLoader = new ClasspathLoader()
        JsonSlurper jsonSlurper = new JsonSlurper()
        InputStream jsonInputStream = classpathLoader.getInputStream(filename)
        Map jsonMap = jsonSlurper.parseText(jsonInputStream.getText('UTF-8'))
        Map configMap = jsonMap.get('configuration')

        this.inputFile = configMap.get('inputFile')
        this.outputDir = configMap.get('outputDir')
        this.allowedGradings = configMap.get('allowedGradings').split(',')
        this.threshold = configMap.get('threshold')
        this.bonus = configMap.get('bonus')
        this.wantThreshold = configMap.get('wantThreshold')
    }
}
