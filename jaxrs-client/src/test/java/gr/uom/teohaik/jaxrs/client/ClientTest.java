
package gr.uom.teohaik.jaxrs.client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.Before;
import org.junit.Test;


/**
 *
 * @author Thodoris
 */
public class ClientTest {

    private Client client;
    private WebTarget targetUnderTest;
    private WebTarget processor;

    @Before
    public void setUp() {
        client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 100);
        client.property(ClientProperties.READ_TIMEOUT, 500);
        targetUnderTest = client.target("http://localhost:8080/supplier/resources/messages");
        this.processor = client.target("http://localhost:8080/processor/resources/processors/beautify");
    }
    
     @Test
     public void fetchMessage() {
          String message = targetUnderTest.request().get(String.class);
          System.out.println("Message fetched = "+message);
     }
     
     @Test
     public void fetchMessageWithSupplier() throws InterruptedException, ExecutionException {
          Supplier<String> messageSupplier = () -> targetUnderTest.request().get(String.class);
          CompletableFuture.supplyAsync(messageSupplier).thenAccept(this::consume).get();
     }
     
     void consume(String message){
         System.out.println("Message from consumer = "+message);
     }
     
     @Test
     public void fetchMessageWithSupplierAndSendBack() throws InterruptedException, ExecutionException {
          Supplier<String> messageSupplier = () -> targetUnderTest.request().get(String.class);
          CompletableFuture.supplyAsync(messageSupplier).thenAccept(this::consumeAndSendBack).get();
     }
     
     void consumeAndSendBack(String message){
         message = message + ",  how you doin? ";
         targetUnderTest.request().post(Entity.text(message));
     }
     
     @Test
     public void processMessage() throws InterruptedException, ExecutionException {
          Supplier<String> messageSupplier = () -> targetUnderTest.request().get(String.class);
          CompletableFuture.supplyAsync(messageSupplier)
                  .thenApply(this::process)
                  .thenAccept(this::consumeAndSendBack)
                  .get();
     }
     
     String process(String input){
        Response response = this.processor.request().post(Entity.text(input));
        return response.readEntity(String.class);
     }
     
      @Test
     public void processMessageWithPool() throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(5);
          Supplier<String> messageSupplier = () -> targetUnderTest.request().get(String.class);
          CompletableFuture.supplyAsync(messageSupplier, pool)
                  .thenApply(this::process)
                  .exceptionally(this::exHandler)
                  .thenAccept(this::consumeAndSendBack)
                  .get();
     }
     
     String exHandler(Throwable ex){
         return "Sorry, we are overloaded!";
     }
     
}
