import java.io.File;
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
                    .filter(Files::isRegularFile).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        System.out.println(listFiles.size());
        for (Path f : listFiles) {
//            String[] s = new String[2];
//            s = f.getFileName().toString().split('');
            if (f.getFileName().toString().equals(file) && f.getParent().equals(dir)) {
                System.out.println(f
//                        + " Поток " + Thread.currentThread().getName()
                );
            }
//            System.out.println(f + " Поток " + Thread.currentThread().getName());
        }
    }
}
