package gr.uom.teohaik.supplier.messaging.boundary;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 *
 * @author Theodore Chaikalis
 */
@Path("messages")
public class MessagesResource {

    
    @GET
    public String message(){
        return "hey duke!";
    }
    
}

