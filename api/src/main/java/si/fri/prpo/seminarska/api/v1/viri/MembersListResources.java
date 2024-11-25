package si.fri.prpo.seminarska.api.v1.viri;

import com.kumuluz.ee.cors.annotations.CrossOrigin;
import si.fri.prpo.seminarska.dtos.PaginatedResponse;
import si.fri.prpo.seminarska.entitete.Event;
import si.fri.prpo.seminarska.entitete.Member;
import si.fri.prpo.seminarska.entitete.CertificateOfEnrollment;
import si.fri.prpo.seminarska.zrna.EventsListBean;
import si.fri.prpo.seminarska.zrna.MembersListBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
@Path("members")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@CrossOrigin(supportedMethods = "GET, POST, HEAD, DELETE, OPTIONS, PATCH")
public class MembersListResources {

    @Inject
    private MembersListBean membersListBean;

    @Inject
    private EventsListBean eventsListBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers() {
        List<Member> members = membersListBean.getMemberList();
        if(members != null) {
            if(members.isEmpty()){
                return Response.status(Response.Status.OK).entity("List is empty").build();
            }
            return Response.ok(members).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Members list not found").build();
    }

    @GET
    @Path("paginated")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaginatedMembersWithMetadata(@QueryParam("page") int page, @QueryParam("size") int size) {
        if (page < 1 || size < 1) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Page and size parameters must be greater than 0.")
                    .build();
        }

        int offset = (page - 1) * size; // Calculate the offset
        List<Member> members = membersListBean.getPaginatedMembers(offset, size);

        if (members.isEmpty()) {
            return Response.status(Response.Status.NO_CONTENT).entity("No members found for the given page.").build();
        }

        // Fetch total member count
        long totalCount = membersListBean.getTotalMemberCount();

        // Calculate total pages
        int totalPages = (int) Math.ceil((double) totalCount / size);
        try {
            // Build the response with metadata
            PaginatedResponse response = new PaginatedResponse(members, page, size, totalCount, totalPages);
            return Response.ok()
                    .entity(response)
                    .build();
        }catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("{memberId}")
    public Response getMember(@PathParam("memberId") long memberId) {
        try {
            Member member = membersListBean.getMemberById(memberId);

            // Check if member list is empty (i.e., no member with that ID exists)
            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }

            // Return the found member (assumes only one member will match the ID)
            return Response.ok(member).build();

        } catch (Exception e) {
            // Handle the case where memberId cannot be parsed to Long
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
    @GET
    @Path("pending")
    public Response getPendingMembers() {
        List<Member> pendingMembers = membersListBean.getPendingMembersList();

        if(pendingMembers != null) {
            if(pendingMembers.isEmpty()){
                return Response.status(Response.Status.NO_CONTENT).entity("No more pending members.\nGood job!").build();
            }
            return Response.ok(pendingMembers).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Pending members list not found").build();
    }
    @GET
    @Path("pending/{memberId}")
    public Response getPendingMember(@PathParam("memberId") long memberId) {
        try{
            Member member = membersListBean.getMemberById(memberId);
            List<Member> existingMembers = membersListBean.getExistingMembers(member);
            if (existingMembers.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("No existing members like this exist" + memberId)
                        .build();
            }
            System.out.println(member.getName() + " "+ member.getSurname());
            System.out.println("\nexisting members count: " + existingMembers.size());
            return Response.ok(existingMembers).build();

        } catch (NumberFormatException e) {
            // Handle the case where memberId cannot be parsed to Long
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid ID format: " + memberId)
                    .build();
        }
    }

    @DELETE
    @Path("{memberId}")
    @Transactional
    public Response deleteMember(@PathParam("memberId") long memberId) {
        try {
            boolean isDeleted = membersListBean.deleteMember(memberId);

            if (isDeleted) {
                return Response.ok("Member with ID " + memberId + " successfully deleted").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid ID format: " + memberId)
                    .build();
        }
    }

    @PUT
    @Path("{memberId}")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMember(@PathParam("memberId") long memberId, Member updatedMember) {
        try {

            // Update the member's details
            updatedMember.setId(memberId);
            Member member = membersListBean.updateMember(memberId, updatedMember);

            return Response.ok(member).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Transactional
    public Response addMember(Member member) {
        // Validate the incoming Member object
        Error memberIsValid = membersListBean.validateMember(member);

        // Call the MembersListBean to persist the member
        if(memberIsValid.getMessage() == null) {
            membersListBean.addToPending(member);

            // Return the created member with a success status
            return Response.status(Response.Status.CREATED)
                    .entity(member)
                    .build();
        }else{
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(memberIsValid.getMessage()).build();
        }


    }

    @PATCH
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response patchMember(@PathParam("id") Long id, Member updatedFields) {
        // Step 1: Fetch the existing member
        Member existingMember = membersListBean.getMemberById(id);
        if (existingMember == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Member with ID " + id + " not found.")
                    .build();
        }

        // Step 2: Update only the fields that are not null in the incoming data
        if (updatedFields.getName() != null) {
            existingMember.setName(updatedFields.getName());
        }
        if (updatedFields.getSurname() != null) {
            existingMember.setSurname(updatedFields.getSurname());
        }
        if (updatedFields.getDateOfBirth() != null) {
            existingMember.setDateOfBirth(updatedFields.getDateOfBirth());
        }
        if (updatedFields.getHomeAddress() != null) {
            existingMember.setHomeAddress(updatedFields.getHomeAddress());
        }
        if (updatedFields.getCity() != null) {
            existingMember.setCity(updatedFields.getCity());
        }
        if (updatedFields.getZipCode() != null) {
            existingMember.setZipCode(updatedFields.getZipCode());
        }
        if (updatedFields.getEmail() != null) {
            existingMember.setEmail(updatedFields.getEmail());
        }
        if (updatedFields.getPhoneNumber() != null) {
            existingMember.setPhoneNumber(updatedFields.getPhoneNumber());
        }
        if (updatedFields.getStatus() != null) {
            existingMember.setStatus(updatedFields.getStatus());
        }
        if (updatedFields.getPending() != null) {
            existingMember.setPending(updatedFields.getPending());
        }

        // Step 3: Persist the changes
        membersListBean.updateMember(id, existingMember);

        // Step 4: Return the updated member
        return Response.ok(existingMember).build();
    }

    @GET
    @Path("{memberId}/events")
    public Response getMemberEvents(@PathParam("memberId") long memberId) {
        try {
            if(membersListBean.getMemberById(memberId) == null){
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }
            List<Event> events = membersListBean.getEvents(memberId);
            // Return the found member (assumes only one member will match the ID)
            if (events != null) {
                if(events.isEmpty()) {
                    return Response.status(Response.Status.OK).entity("No events attended").build();
                }
                return Response.ok(events).build();
            }
            return Response.status(Response.Status.NOT_FOUND).build();

        } catch (Exception e) {
            // Handle the case where memberId cannot be parsed to Long
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("{memberId}/certificates")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCertificateToMember(@PathParam("memberId") long memberId, CertificateOfEnrollment certificate) {
        try {
            // Fetch the member by ID
            Member member = membersListBean.getMemberById(memberId);

            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }

            // Persist the changes
            member = membersListBean.addCertificateOfEnrollment(member, certificate);
            if (member != null) {
                return Response.status(Response.Status.CREATED)
                        .entity("Certificate successfully added to member with ID: " + memberId)
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("addCertificateOfEnrollment failed").build();


        }catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("{memberId}/certificates")
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getCertificateForMember(@PathParam("memberId") long memberId) {
        try {
            // Fetch the member by ID
            Member member = membersListBean.getMemberById(memberId);

            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }

            // Persist the changes
            List<CertificateOfEnrollment> enrollments = member.getEnrollments();
            if (enrollments != null) {
                if(enrollments.isEmpty()) {
                    return Response.status(Response.Status.OK).entity("No enrollments yet.").build();
                }
                return Response.status(Response.Status.CREATED)
                        .entity(enrollments)
                        .build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("CertificateOfEnrollment list is null.").build();


        }catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(e.getMessage())
                    .build();
        }
    }


    @POST
    @Transactional
    @Path("{memberId}/event/{eventId}")
    public Response getMemberEvents(@PathParam("memberId") long memberId, @PathParam("eventId") long eventId) {
        try {
            Member member = membersListBean.getMemberById(memberId);
            Event event = eventsListBean.getEventById(eventId);
//            System.out.println(event.getAttendingMembers().toString());
//            System.out.println(member.getVisitedEvents().toString());

            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Member not found for ID: " + memberId).build();
            }
            if(member.getVisitedEvents() == null){
                member.setVisitedEvents(new ArrayList<>());
            }
            if (event == null) {
                return Response.status(Response.Status.NOT_FOUND).entity("Event not found").build();
            }
            if(event.getAttendingMembers() == null){
                event.setAttendingMembers(new ArrayList<>());
            }

            // Associate member and event
            Event updatedEvent = null;
            Member updatedMember = null;
            if (!event.getAttendingMembers().contains(member)) {
                event.getAttendingMembers().add(member);
                member.getVisitedEvents().add(event); // Assuming this method exists
                updatedMember = membersListBean.updateMember(member.getId(), member);
                updatedEvent = eventsListBean.updateEvent(event.getId(), event);
            }
            if(updatedMember != null && updatedEvent != null) {
                return Response.ok(updatedEvent).build();
            }
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Something went wrong when Associate member and event")
                    .build();


        } catch (Exception e) {
            // Handle the case where memberId cannot be parsed to Long
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

}
