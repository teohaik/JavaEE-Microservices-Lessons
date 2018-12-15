
package gr.uom.teohaik.asyncjaxrs.boundary;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

/**
 *
 * @author Theodore Chaikalis
 */
@Path("async")
public class AsyncResource {
    
    @Resource
    ManagedExecutorService mes;  //needs beans.xml because...CDI!

      @GET
    public void get(@Suspended AsyncResponse response){
        
        //response.resume(doSomeWork());
        
        //Replaced with this:
        Supplier<String> supplier = this::doSomeWork;
        Consumer<String> consumer = response::resume;
        CompletableFuture
                .supplyAsync(supplier, mes)
                .thenAccept(consumer);
    }
    
    String doSomeWork(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AsyncResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "+ "+System.currentTimeMillis()+ " +";         
    }
}
