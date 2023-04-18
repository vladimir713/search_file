import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        searchFile("system.ini", "c:\\Intel");
    }
    public static void searchFile(String file, String dir) throws IOException {
        List<Path> listDirs = Files.walk(Paths.get(dir))
                .filter(Files::isDirectory).toList();
        for(Path s:listDirs) {
            System.out.println(s.toString());
        }
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (Path p:listDirs) {
            executorService.execute(() -> System.out.println("Поток"));
        }
        executorService.shutdown();
    }
}
