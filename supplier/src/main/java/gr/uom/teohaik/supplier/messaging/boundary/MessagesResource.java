package gr.uom.teohaik.supplier.messaging.boundary;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 * @author Theodore Chaikalis
 */
@Path("messages")
public class MessagesResource {

    
    @GET
    public String message(){
        try {
            Thread.sleep(1000);
           
        } catch (InterruptedException ex) {
            Logger.getLogger(MessagesResource.class.getName()).log(Level.SEVERE, null, ex);
        }
         return "hey duke!";
    }
    
    
    @POST
    public void messageHandle(String message){
        System.out.println("---------------------------------------------------");
        System.out.println("Just received new message: [" + message + "]");
        System.out.println("---------------------------------------------------");
    }
    
}

