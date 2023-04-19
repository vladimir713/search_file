import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws IOException {
        searchFile("mmc.CHM", "c:\\Windows\\Help");
    }
    public static void searchFile(String file, String dir) throws IOException {
        List<Path> listDirs = Files.walk(Paths.get(dir))
                .filter(Files::isDirectory).toList();
        for(Path s:listDirs) {
            System.out.println(s);
        }
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        for (Path p:listDirs) {
            executorService.execute(() -> {
                try {
                    List<Path> listFiles = Files.walk(Paths.get(p.toString()))
                            .filter(Files::isRegularFile).toList();
                    for (Path f: listFiles) {
                        if (f.getFileName().toString().equals(file)) {
                            System.out.println(f.getFileName() + "--------------------------------------------------");
                        }
                        System.out.println(f);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executorService.shutdown();
    }
}
