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
}
