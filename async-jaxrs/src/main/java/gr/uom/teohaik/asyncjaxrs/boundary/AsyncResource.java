package gr.uom.teohaik.asyncjaxrs.boundary;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientProperties;

/**
 *
 * @author Theodore Chaikalis
 */
@Path("async")
public class AsyncResource {
    
    //Continue from: 29 - Dry Pools And Service Stability

    @Resource
    ManagedExecutorService mes;  //needs beans.xml because...CDI!

    private Client client;
    private WebTarget targetUnderTest;
    private WebTarget processor;

    @PostConstruct
    public void setUp() {
        client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        client.property(ClientProperties.READ_TIMEOUT, 5000);
        targetUnderTest = client.target("http://localhost:8080/supplier/resources/messages");
        this.processor = client.target("http://localhost:8080/processor/resources/processors/beautify");
    }

    @GET
    @Path("orchestration")
    public void processMessageWithPool(@Suspended AsyncResponse response) throws InterruptedException, ExecutionException {
        Supplier<String> messageSupplier = () -> targetUnderTest.request().get(String.class);
        CompletableFuture.supplyAsync(messageSupplier, mes)
                .thenApply(this::process)
                .exceptionally(this::exHandler)
                .thenApply(this::consumeAndSendBack)
                .thenAccept(response::resume);
    }

    String process(String input) {
        Response response = this.processor.request().post(Entity.text(input));
        return response.readEntity(String.class);
    }

    String consumeAndSendBack(String message) {
        message = message + ",  how you doin? ";
        targetUnderTest.request().post(Entity.text(message));
        return message;
    }

    String exHandler(Throwable ex) {
        return "Sorry, we are overloaded! "+ex.getMessage();
    }

    @GET
    public void get(@Suspended AsyncResponse response) {

        //response.resume(doSomeWork());
        //Replaced with this:
        Supplier<String> supplier = this::doSomeWork;
        Consumer<String> consumer = response::resume;
        CompletableFuture
                .supplyAsync(supplier, mes)
                .thenAccept(consumer);
    }

    String doSomeWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AsyncResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "+ " + System.currentTimeMillis() + " +";
    }
}
