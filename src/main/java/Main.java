import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(50);

        for (int i=0; i<1; i++) {
            executorService.submit(new DbWriteInfo(i));
        }
        executorService.shutdown();
        System.out.println("All tasks has been submitted!");
        executorService.awaitTermination(1, TimeUnit.DAYS);
    }
}
