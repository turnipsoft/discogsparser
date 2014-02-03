package dk.turnipsoft.discogsparser.util

/**
 * This class provides static assertion validations to provide a
 * clean way to fail early validation.
 */
class ValidateAndThrowIf {
    public static String DEFAULT_IS_NULL_MESSAGE = 'Null is not allowed in this situation'
    public static String DEFAULT_IS_NOT_INSTANCE_OF_MESSAGE = '%s is not instance of %s'
    public static String DEFAULT_IS_NOT_ASSIGNABLE_FROM_MESSAGE = '%s is not assignable from %s'

    /**
     * Checks whether provided object is null or not and throws illegalArgumentException
     * if it is.
     * @param object the object to check
     * @param message optional message to send with the exception
     */
    public static void isNull(Object object, String message = null) {
        if(!object) {
            String exceptionMessage = message ?: DEFAULT_IS_NULL_MESSAGE
            throw new IllegalArgumentException(exceptionMessage)
        }
    }

    public static void isNotInstanceOf(Object object, Class clazz, String message = null) {
        ValidateAndThrowIf.isNull(clazz)
        boolean isInstanceOf = clazz.isInstance(object)

        if(!isInstanceOf) {
            String exceptionMessage = message ?: String.format(DEFAULT_IS_NOT_INSTANCE_OF_MESSAGE, object?.class?.name, clazz.name)
            throw new IllegalArgumentException(exceptionMessage)
        }
    }

    public static void isNotAssignalbleFrom(Class objectClass, Class clazz, String message = null) {
        ValidateAndThrowIf.isNull(objectClass)
        ValidateAndThrowIf.isNull(clazz)
        boolean isAssignableFrom = clazz.isAssignableFrom(objectClass)

        if(!isAssignableFrom) {
            String exceptionMessage = message ?: String.format(DEFAULT_IS_NOT_ASSIGNABLE_FROM_MESSAGE, objectClass.name, clazz.name)
            throw new IllegalArgumentException(exceptionMessage)
        }
    }

}
