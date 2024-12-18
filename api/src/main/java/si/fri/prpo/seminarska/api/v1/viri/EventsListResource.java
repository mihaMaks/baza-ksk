package si.fri.prpo.seminarska.api.v1.viri;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import si.fri.prpo.seminarska.dtos.PaginatedResponse;
import si.fri.prpo.seminarska.entitete.Event;
import si.fri.prpo.seminarska.entitete.Member;
import si.fri.prpo.seminarska.zrna.EventsListBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@ApplicationScoped
@Path("events")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(supportedMethods = "GET, POST, HEAD, DELETE, OPTIONS, PATCH")

public class EventsListResource {

    @Inject
    EventsListBean eventsListBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEvents() {
        List<Event> events = eventsListBean.getEventsList();
        System.out.println(events.isEmpty());
        if(events != null) {
            if(events.isEmpty()){
                return Response.status(Response.Status.OK).entity("List is empty").build();
            }
            return Response.ok(events).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Events list not found").build();
    }
    @GET
    @Path("paginated")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaginatedEvents(@QueryParam("page") int page, @QueryParam("size") int size) {

        if (page < 1 || size < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Page and size parameters must be greater than 0.")
                    .build();
        }

        int offset = (page - 1) * size; // Calculate the offset
        List<Event> events = eventsListBean.getPaginatedEvents(offset, size);

        if (events.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("No events found for the given page.").build();
        }

        // Fetch total Event count
        long totalCount = eventsListBean.getTotalEventCount();

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalCount / size);

        // Build the response with metadata
        return Response.ok()
                .entity(new PaginatedResponse(events, page, size, totalCount, totalPages))
                .build();
    }

    @GET
    @Path("{eventId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEventById(@PathParam("eventId") long eventId) {
        Event event = eventsListBean.getEventById(eventId);
        if(event != null) {
            return Response.ok(event).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Events list not found").build();
    }


}

