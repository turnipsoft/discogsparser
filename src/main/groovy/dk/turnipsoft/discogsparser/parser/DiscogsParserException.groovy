package dk.turnipsoft.discogsparser.parser

/**
 * Created by shartvig on 03/02/14.
 */
class DiscogsParserException extends Exception {

    DiscogsParserException() {
        super()
    }

    DiscogsParserException(String message) {
        super(message)
    }

    DiscogsParserException(String message, Throwable cause) {
        super(message, cause)
    }

    DiscogsParserException(Throwable cause) {
        super(cause)
    }
}
