/* Copyright (c) 2023, Vladimir Chugunov
 */

/**
 * Также есть вариант программы с использованием Nio2
 */

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static AtomicInteger countSmallFiles = new AtomicInteger(0);

    public static void main(String[] args) {
        searchFile("*nvwksS*", "c:\\Windows\\help");
    }
    public static void searchFile(String file, String dir) {
        try {
            List<Path> listDirs = Files.walk(Paths.get(dir))
                    .filter(Files::isDirectory)
                    .toList();

            file = file.replace("*", ".*");                         // Меняем на регулярное выражение

/** Для каждой директории создается свой поток.
 *  Для путей, не содержащих файлы, тоже создаются потоки. Можно доработать программу
 *  и не запускать такие потоки.
 */

            CountDownLatch latch = new CountDownLatch(listDirs.size()); // Счетчик рабочих потоков

            for (Path p:listDirs) {
                new Thread(new MyRunnable(p, file, latch)).start();    // Можно реализовать и через пул потоков ExecuteService
            }
            latch.await();
        } catch (UncheckedIOException e) {                      // Может быть очень круто,
            System.out.println("Ошибка доступа к " + dir);      // лучше поискать более специализированные исключения :)
        } catch (IOException e) {
            System.out.println("Путь " + dir + " не существует");                   // Можно и логгер использовать
        } catch (InterruptedException e) {
            System.out.println("Проблемы со счетчиком потоков");
        }

        System.out.println(countSmallFiles);
    }
}
