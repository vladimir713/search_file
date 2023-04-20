import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        searchFile("*nv*", "c:\\Windows\\help");
    }
    public static void searchFile(String file, String dir) {
        try {
            List<Path> listDirs = Files.walk(Paths.get(dir))
                    .filter(Files::isDirectory)
                    .toList();

            file = file.replace("*", ".*");                         // Меняем на регулярное выражение

            // Для путей, не содержащих файлы, тоже создаются потоки. Можно доработать программу
            // и не запускать такие потоки

            for (Path p:listDirs) {
                new Thread(new MyRunnable(p, file)).start();    // Можно реализовать и через пул потоков ExecuteService
            }
        } catch (UncheckedIOException e) {                      // Может быть очень круто,
            System.out.println("Ошибка доступа к " + dir);      // лучше поискать более специализированные исключения :)
        } catch (IOException e) {
            System.out.println("Путь " + dir + " не существует");                   // Можно и логгер использовать
        }
    }
}
