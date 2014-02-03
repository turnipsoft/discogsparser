package dk.turnipsoft.discogsparser.model

/**
 * Created by shartvig on 03/02/14.
 */
class Grading {

    public static final String GRADING_DISC = 'DISC'
    public static final String GRADING_SLEEVE = 'SLEEVE'

    GradingType grading
    String gradingFor
    String comment

    public static Grading getGrading(Listing listing) {

        // determine grading
        return new Grading()
    }

}
