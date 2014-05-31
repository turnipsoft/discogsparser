package dk.turnipsoft.discogsparser.util

/**
 * Created by shartvig on 08/02/14.
 */
class FileUtil {

    public static void writeFile(String filename, List<String> list) {
        filename = filename
        PrintWriter pw = new PrintWriter(new File(filename))

        list.each  {s->
            pw.println(s)
        }

        pw.close()
    }

    public static void writeFile(String filename, StringBuffer list) {
        filename = filename
        PrintWriter pw = new PrintWriter(new File(filename))

        pw.println(list.toString())

        pw.close()
    }

    public static String readFileFromClasspath(String filename) {
        ClasspathLoader cpLoader = new ClasspathLoader()
        InputStream input = cpLoader.getInputStream("$filename")
        StringBuffer buf = new StringBuffer()
        BufferedReader br = new BufferedReader(new InputStreamReader(input))
        String s

        while ((s=br.readLine())!=null) {
            buf.append(s+"\n");
        }

        return buf.toString()
    }

    public static String readFile(String filename) {

        InputStream input = new FileInputStream("$filename")
        StringBuffer buf = new StringBuffer()
        BufferedReader br = new BufferedReader(new InputStreamReader(input))
        String s

        while ((s=br.readLine())!=null) {
            buf.append(s+"\n");
        }

        return buf.toString()
    }
}
