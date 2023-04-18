import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        searchFile("system.ini", "c:\\Dell");
    }
    public static void searchFile(String file, String dir) throws IOException {
        List<Path> listDirs = Files.walk(Paths.get(dir))
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
        for(Path s:listDirs) {
            System.out.println(s.toString());
        }
    }
}
