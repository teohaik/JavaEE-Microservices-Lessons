package gr.uom.teohaik.asyncjaxrs.boundary;

import java.util.concurrent.RejectedExecutionException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Theodore Chaikalis
 */
@Provider
public class ServerOverloadHandler implements ExceptionMapper<RejectedExecutionException> {

    @Override
    public Response toResponse(RejectedExecutionException exception) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .header("overload-reason", exception.toString())
                .build();
    }
}
