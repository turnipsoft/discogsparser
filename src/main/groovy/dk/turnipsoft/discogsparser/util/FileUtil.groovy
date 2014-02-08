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
}
