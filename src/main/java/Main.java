/* Copyright (c) 2023, Vladimir Chugunov
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

public class Main {

    private static final String POST_URL = "http://localhost:8080/upload";

    public static List<Path> list= new CopyOnWriteArrayList<>();

    public static void main(String[] args) {

        searchFile("*chm", "c:\\Windows\\Help");
    }

    /**
     * Могу составить вариант программы с использованием Nio2 с применением FileVisitor
     * В данной реализации для каждой директории создается свой поток.
     * Для путей, не содержащих файлы, тоже создаются потоки. Могу доработать программу
     * и не запускать такие потоки.
     * Используется интерфейс Runnable.
     * Также возможно реализовать многопоточность с использованием пула потоков ExecuteServiсe
     *
     * @param file Строка с именем файла для поиска. Используется шаблон * - любое количество символов.
     *             В последствии * меняется на регулярное выражение.
     * @param dir Директория, где будет осуществляться поиск файлов
     * @latch Счетчик рабочих потоков
     */
    public static void searchFile(String file, String dir) {
        try {
            List<Path> listDirs = Files.walk(Paths.get(dir))
                    .filter(Files::isDirectory)
                    .toList();

            file = file.replace("*", ".*");

            CountDownLatch latch = new CountDownLatch(listDirs.size());

            for (Path p:listDirs) {
                new Thread(new MyRunnable(p, file, latch)).start();
            }

            latch.await();

            for (Path p:list) {
                System.out.println(p + " размер = " + Files.size(p));
            }
            System.out.print("Всего найдено файлов: " + list.size() + "\n\n" + "Копировать их по сети? (y/n) ");
            Scanner sc = new Scanner(System.in);
            if (sc.nextLine().equals("y")) {
                transferFile(list, POST_URL);
            };


        } catch (UncheckedIOException e) {                      // Может быть очень круто,
            System.out.println("Ошибка доступа к " + dir);      // лучше поискать более специализированные исключения :)
        } catch (IOException e) {
            System.out.println("Путь " + dir + " не существует");                   // Можно и логгер использовать
        } catch (InterruptedException e) {
            System.out.println("Проблемы со счетчиком потоков");
        }
    }

    /**
     * Пока так реализовал. Но не проверял. Надо поднять HTTP Server и проверить.
     * В перспективе - хочу попробовать использовать OkHttp для отправки по сети файлов
     * Дополнительные варианты реализации - через CloseableHttpClient, MultipartEntityBuilder, ...
     * @param list Список файлов для отправки
     * @param uri Адрес - точка входа сервера, контроллер которого будет обрабатывать запрос
     * @throws FileNotFoundException Исключение, если файл не будет найден
     */
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
    }

}
