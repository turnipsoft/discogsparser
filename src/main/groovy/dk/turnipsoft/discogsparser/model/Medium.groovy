package dk.turnipsoft.discogsparser.model

import org.codehaus.jackson.annotate.JsonIgnoreProperties

/**
 * Created by shartvig on 03/02/14.
 */
@JsonIgnoreProperties(['CD','vinyl','cassette','movie','cd'])
class Medium {

    ReleaseType releaseType
    MasterReleaseType masterReleaseType

    public Medium() {

    }

    public Medium(ReleaseType releaseType) {
        this.releaseType = releaseType
        if (releaseType in [ReleaseType.CDALBUM, ReleaseType.CDMAXI, ReleaseType.CDS]) {
            this.masterReleaseType = MasterReleaseType.CD
        } else if (releaseType in [ReleaseType.VINYLALBUM, ReleaseType.VINYLEP, ReleaseType.VINYLSEVEN, ReleaseType.VINYLTEN]) {
            this.masterReleaseType = MasterReleaseType.VINYL
        } else if (releaseType == ReleaseType.CASSETTE) {
            this.masterReleaseType = MasterReleaseType.CASSETTE
        } else if (releaseType == ReleaseType.BLURAY ) {
            this.masterReleaseType = MasterReleaseType.BLURAY
        } else if ( releaseType == ReleaseType.DVD ) {
            this.masterReleaseType = MasterReleaseType.DVD
        }
    }

    boolean isCD() {
        MasterReleaseType.CD == masterReleaseType
    }

    boolean isVinyl() {
        MasterReleaseType.VINYL == masterReleaseType
    }

    boolean isCassette() {
        MasterReleaseType.CASSETTE == masterReleaseType

    }

    boolean isMovie() {
        MasterReleaseType.DVD == masterReleaseType || MasterReleaseType.DVD == masterReleaseType
    }
}
