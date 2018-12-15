
package gr.uom.teohaik.jaxrs.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author Thodoris
 */
public class ClientTest {

    private Client client;
    private WebTarget targetUnderTest;

    @Before
    public void setUp() {
        client = ClientBuilder.newClient();
        targetUnderTest = client.target("http://localhost:8080/supplier/resources/messages");
    }
    
     @Test
     public void fetchMessage() {
          String message = targetUnderTest.request().get(String.class);
          System.out.println("Message fetched = "+message);
     }
     
     @Test
     public void fetchMessageWithSupplier() {
          String message = targetUnderTest.request().get(String.class);
          System.out.println("Message fetched = "+message);
     }
}
