package dk.turnipsoft.discogsparser.util

/**
 * This class provides a methods to load resources from classpath
 */
class ClasspathLoader {

    /**
     * Returns the thread context classloader if it is accessible otherwise
     * the classloader of this class is returned.
     * @return classloader
     */
    public ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClasspathLoader.class.getClassLoader();
        }
        return cl;
    }

    /**
     * Checks for existence of a classpath resource.
     * @see java.lang.ClassLoader#getResource(String)
     * @param path the location of the resource
     * @return true if the resource exists otherwise false
     */
    public boolean exists(String path) {
        boolean result = false
        URL resourceUrl = getDefaultClassLoader().getResource(path)

        if (resourceUrl) {

            File file = new File(resourceUrl.toURI())
            file.exists()
            result = file.isFile()
        }

        return result
    }

    /**
     * Returns an input stream to a classpath resource.
     *
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     * @param path the location of the resource
     * @return inputStream of the resource
     * @throws IllegalArgumentException if path is null or not file was found or could not be loaded as an input stream.
     */
    public InputStream getInputStream(String path) {
        ValidateAndThrowIf.isNull(path)

        if(!exists(path)) {
            throw new IllegalArgumentException("['${path}'] doesn't exist or is not af file" )
        }
        InputStream inputStream = getDefaultClassLoader().getResourceAsStream(path)

        ValidateAndThrowIf.isNull(inputStream, "Could not load ['${path}'] from classpath")

        return inputStream
    }
}
