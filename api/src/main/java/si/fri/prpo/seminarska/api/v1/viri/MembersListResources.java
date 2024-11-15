package si.fri.prpo.seminarska.api.v1.viri;

import si.fri.prpo.seminarska.entitete.Member;
import si.fri.prpo.seminarska.entitete.NakupovalniSeznam;
import si.fri.prpo.seminarska.zrna.MembersListBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
@Path("members")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MembersListResources {

    @Inject
    private MembersListBean membersListBean;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembers() {
        List<Member> members = membersListBean.getMemberList();
        if(members != null) {
            if(members.isEmpty()){
                return Response.status(Response.Status.NO_CONTENT).entity("List is empty").build();
            }
            return Response.ok(members).build();

        }
        return Response.status(Response.Status.NOT_FOUND).entity("Members list not found").build();
    }
    @GET
    @Path("{memberId}")
    public Response getMember(@PathParam("memberId") String memberId) {
        try {
            // Convert memberId to Long
            Long id = Long.parseLong(memberId);

            Member member = membersListBean.getMemberById(id);

            // Check if member list is empty (i.e., no member with that ID exists)
            if (member == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Member not found for ID: " + memberId)
                        .build();
            }

            // Return the found member (assumes only one member will match the ID)
            return Response.ok(member).build();

        } catch (NumberFormatException e) {
            // Handle the case where memberId cannot be parsed to Long
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid ID format: " + memberId)
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

    @POST
    @Transactional
    public Response handleForm(Member member) {
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


}
