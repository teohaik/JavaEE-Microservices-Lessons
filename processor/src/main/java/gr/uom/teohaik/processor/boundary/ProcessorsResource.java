package gr.uom.teohaik.processor.boundary;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 *
 * @author Theodore Chaikalis
 */
@Path("processors")
public class ProcessorsResource {
    
    @POST
    @Path("beautify")
    public String process(String input){
        return "## " + input + " ##";
    }

}
