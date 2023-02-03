import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

class FileHelpers {
    static List<File> getFiles(Path start) throws IOException {
        File f = start.toFile();
        List<File> result = new ArrayList<>();
        if(f.isDirectory()) {
            File[] paths = f.listFiles();
            for(File subFile: paths) {
                result.addAll(getFiles(subFile.toPath()));
            }
        }
        else {
            result.add(start.toFile());
        }
        return result;
    }
    static String readFile(File f) throws IOException {
        return new String(Files.readAllBytes(f.toPath()));
    }
}

class Handler implements URLHandler {
    List<File> paths;
    Handler(String directory) throws IOException {
      this.paths = FileHelpers.getFiles(Paths.get(directory));
    }
    public String handleRequest(URI url) throws IOException {
       if (url.getPath().equals("/")) {
           return String.format("Use /search?q=... to search the files!", paths.size());
       } else if (url.getPath().equals("/search")) {
           String[] parameters = url.getQuery().split("=");
           if (parameters[0].equals("q")) {
               List<File> foundPaths = new ArrayList<>();
               for(File f: paths) {
                   if(f.toString().contains(parameters[1])) {
                       foundPaths.add(f);
                   }
               }
               Collections.sort(foundPaths);
               String result = "";
               String border = "=".repeat(72);
               for (File f: foundPaths) {
                  String contents = FileHelpers.readFile(f);
                  contents = contents.length() > 100 ? contents.substring(0, 100) : contents;
                  result += border + "\n" + f.toString() + ":\n" + contents + "...\n" + border + "\n";
               }
               if(foundPaths.size() == 0) {
                 return "No files found";
               }
               else {
                 return String.format("Found %d files:\n%s", foundPaths.size(), result);
               }
           }
           else {
               return "Couldn't find query parameter q";
           }
       }
       else {
           return "Don't know how to handle that path!";
       }
    }
}

/**

  Usage:

  java FileServer <port> <directory>

  Starts the server on <port>, with a Handler configured to search the given
  directory when processing /search requests.

*/
class FileServer {
    public static void main(String[] args) throws IOException {
        if(args.length < 1){
            System.out.println("Missing port number! Try any number between 1024 to 49151 as the first argument");
            return;
        }
        if(args.length < 2){
            System.out.println("Missing directory to search! Give the path to a directory of text files to search.");
            return;
        }

        int port = Integer.parseInt(args[0]);

        Server.start(port, new Handler(args[1]));
    }
}

