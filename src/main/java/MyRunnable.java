import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MyRunnable implements Runnable{
    private Path dir;
    private String file;

    public MyRunnable(Path dir, String file) {
        this.dir = dir;
        this.file = file;
    }

    @Override
    public void run() {
        List<Path> listFiles;
        try {
            listFiles = Files.walk(Paths.get(dir.toString()))
                    .filter(Files::isRegularFile)
                    .filter(Files -> Files.getParent().equals(dir))
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Path f : listFiles) {
            if (f.getFileName().toString().matches(file)) {
                System.out.println(f);
            }
        }
    }
}
