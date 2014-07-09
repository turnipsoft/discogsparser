package dk.turnipsoft.discogsparser.parser

/**
 * Created by shartvig on 09/07/14.
 */
class SimpleCsvTokenizer {

    String stringToTokenize
    int currentIdx = 0

    public SimpleCsvTokenizer(String string) {
        this.stringToTokenize = string
    }

    public String nextElement() {
        char c = 0
        boolean ignore = false
        StringBuffer b = new StringBuffer()


        for (int i=currentIdx;i<stringToTokenize.length();i++) {
            char currentChar = stringToTokenize.charAt(i)
            if (currentChar == '"') {
                ignore = !ignore
                currentIdx++;
                if (currentIdx==stringToTokenize.length()) {
                    return b
                }
                continue
            }
            if (currentChar == ',' && !ignore) {
                currentIdx++
                return b
            }

            b.append(currentChar)
            currentIdx++
        }
    }
}
