package microservices;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Theodore Chaikalis
 */
public class ExceptionalPipelinesTest {

    @Test
    public void initialWithError() throws InterruptedException, ExecutionException {
        CompletableFuture.supplyAsync(this::exceptional).get();
    }

    @Test
    public void handleProperly() throws InterruptedException, ExecutionException {
        CompletableFuture.supplyAsync(this::exceptional)
                .exceptionally(this::transformException)
                .thenAccept(this::consume)
                .get();
    }

    @Test
    public void handleProperlyWithHandle() throws InterruptedException, ExecutionException {
        CompletableFuture.supplyAsync(this::exceptional)
                .handle(this::handleException)
                .thenAccept(this::consume)
                .get();
    }

    @Test
    public void handleNoExceptionWithHandle() throws InterruptedException, ExecutionException {
        CompletableFuture.supplyAsync(this::notExceptional)
                .handle(this::handleException)
                .thenAccept(this::consume)
                .get();
    }

    public void consume(String message) {
        System.out.println("Message = " + message);
    }

    public String exceptional() {
        throw new IllegalStateException("happens");
    }

    public String notExceptional() {
        return "no exception here!";
    }

    public String transformException(Throwable t) {
        return t.toString();
    }

    public String handleException(String valid, Throwable ex) {
        return "Valid = " + valid + " --- Throwable = " + ex;
    }

    ////////////////////////////////////////////
    // Configuring the Completable Future:

    @Test
    public void forkJoinConfiguration() throws InterruptedException {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "5");
        
        for (int i = 0; i < 200; i++) {
            CompletableFuture.runAsync(this::slow);
        }
        Thread.sleep(300000);
        
        //Run it and check threads in Visual VM
        //Run first with 5 and then with 50 threads
    }

    void slow() {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ExceptionalPipelinesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void forkJoinConfigurationWithExecutorServiceForServers() throws InterruptedException {
        ExecutorService custom = Executors.newCachedThreadPool();
        for (int i = 0; i < 200; i++) {
            CompletableFuture.runAsync(this::slow, custom);
        }
        Thread.sleep(300000);
        
        //Run it and check threads in Visual VM
        //Run first with 5 and then with 50 threads
    }
}
