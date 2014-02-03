package dk.turnipsoft.discogsparser.model

/**
 * Created by shartvig on 03/02/14.
 */
class Grading {

    public static final String GRADING_DISC = 'DISC'
    public static final String GRADING_SLEEVE = 'SLEEVE'

    GradingType grading
    String gradingFor

    public static Grading getGrading(String gradingDescription, String gradingFor) {

        Grading g = new Grading()
        g.gradingFor = gradingFor
        if (gradingDescription=='Mint (M)') {
            g.grading = GradingType.MINT
        } else if (gradingDescription=='Near Mint (NM or M-)') {
            g.grading = GradingType.NEARMINT
        } else if (gradingDescription=='Very Good Plus (VG+)') {
            g.grading = GradingType.VERYGOODPLUS
        } else if (gradingDescription=='Very Good (VG)') {
            g.grading = GradingType.VERYGOOD
        } else if (gradingDescription=='Good Plus (G+)') {
            g.grading = GradingType.GOODPLUS
        } else if (gradingDescription=='Good (G)') {
            g.grading = GradingType.GOOD
        } else if (gradingDescription=='Fair (F)') {
            g.grading = GradingType.FAIR
        } else if (gradingDescription=='Poor (P)') {
            g.grading = GradingType.POOR
        } else if (gradingDescription=='Generic') {
            g.grading = GradingType.NONE
        } else if (gradingDescription=='None') {
            g.grading = GradingType.NONE
        } else if (gradingDescription=='No Cover') {
            g.grading = GradingType.NONE
        } else {
            throw new RuntimeException("unknown grading : $gradingDescription")
        }
        return g

    }

}
