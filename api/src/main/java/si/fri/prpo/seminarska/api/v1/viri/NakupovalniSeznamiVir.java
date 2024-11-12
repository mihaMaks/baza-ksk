package si.fri.prpo.seminarska.api.v1.viri;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

import com.kumuluz.ee.cors.annotations.CrossOrigin;

import si.fri.prpo.seminarska.entitete.NakupovalniSeznam;
import si.fri.prpo.seminarska.zrna.NakupovalniSeznamiZrno;
import si.fri.prpo.seminarska.zrna.UpravljanjeNakupovalnihSeznamovZrno;

@ApplicationScoped
@Path("seznami")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(supportedMethods = "GET, POST, HEAD, DELETE, OPTIONS")
public class NakupovalniSeznamiVir {

    @Context
    protected UriInfo uriInfo;

    @Inject
    private UpravljanjeNakupovalnihSeznamovZrno upravljanjeNakupovalnihSeznamovZrno;

    @Inject
    private NakupovalniSeznamiZrno nakupovalniSeznamiZrno;

    // TODO: missing implementation
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response pridobiNakupovalneSezname() {
        // Use the injected NakupovalniSeznamiZrno bean to get the shopping list from the database
        List<NakupovalniSeznam> seznami = nakupovalniSeznamiZrno.pridobiNakupovalneSezname();

        if (seznami != null) {
            // If the shopping list is found, return it with a 200 OK response
            return Response.ok(seznami).build();
        } else {
            // If not found, return a 404 Not Found response
            return Response.status(Response.Status.NOT_FOUND).entity("Shopping list not found").build();
        }
    }


}