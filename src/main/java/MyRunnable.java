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
                    .filter(Files -> Files.getParent().equals(dir)) // Чтобы не было повторов найденных файлов.
                    .toList();                                     // Пока не нашел метода выделить в текущей папке
        } catch (IOException e) {                                 // только файлы из этой директории, но не из вложенных
            throw new RuntimeException(e);
        }

        for (Path f : listFiles) {
            if (f.getFileName().toString().matches(file)) {                  // file теперь уже с регулярным выражением
                System.out.println(f);
            }
        }
    }
}
