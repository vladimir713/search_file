/* Copyright (c) 2023, Vladimir Chugunov
 */

/**
 * Также есть вариант программы с использованием Nio2
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static AtomicInteger countSmallFiles = new AtomicInteger(0);
    public static List<Path> list= new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        searchFile("*", "c:\\Windows\\Help");
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

            for (Path p:list) {
                System.out.println(p + " размер = " + Files.size(p));
            }
            System.out.print("Всего найдено файлов: " + list.size() + "\n\n" + "Копировать их по сети? (y/n) ");
            Scanner sc = new Scanner(System.in);
            if (sc.nextLine().equals("y")) {
                transferFile(list, "http://127.0.0.1:8080/transfery");
            };


        } catch (UncheckedIOException e) {                      // Может быть очень круто,
            System.out.println("Ошибка доступа к " + dir);      // лучше поискать более специализированные исключения :)
        } catch (IOException e) {
            System.out.println("Путь " + dir + " не существует");                   // Можно и логгер использовать
        } catch (InterruptedException e) {
            System.out.println("Проблемы со счетчиком потоков");
        }

//        System.out.println(countSmallFiles);

    }
    public static void transferFile(List<Path> list, String uri) throws FileNotFoundException {
        HttpClient client = HttpClient.newHttpClient();
        for (Path p:list) {
            HttpRequest request = HttpRequest.newBuilder()
                  .uri(URI.create(uri))
                  .POST(HttpRequest.BodyPublishers.ofFile(p))
                  .build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                  .thenApply(HttpResponse::body)
                  .thenAccept(System.out::println);
        }
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(uri))
//                .POST(HttpRequest.BodyPublishers.ofFile(Paths.get("c:\\Windows\\Help\\X.log")))
//                .build();
//        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
//                .thenApply(HttpResponse::body)
//                .thenAccept(System.out::println);

    }

}
