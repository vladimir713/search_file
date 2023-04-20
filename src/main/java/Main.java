import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException {
        searchFile("*nv*Y*", "c:\\Windows\\Help");
    }
    public static void searchFile(String file, String dir) throws IOException {
        List<Path> listDirs = Files.walk(Paths.get(dir))
                .filter(Files::isDirectory).toList();
        file = file.replace("*", ".*");

        for (Path p:listDirs) {
            new Thread(new MyRunnable(p, file)).start();
        }
    }
}
