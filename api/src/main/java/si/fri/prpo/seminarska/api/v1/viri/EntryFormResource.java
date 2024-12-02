package si.fri.prpo.seminarska.api.v1.viri;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import si.fri.prpo.seminarska.entitete.Member;
import si.fri.prpo.seminarska.zrna.EntryFormBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

@RequestScoped
@Path("entry-form")
public class EntryFormResource {

    @Inject
    EntryFormBean entryFormBean;

    @POST
    @Path("")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleForm(@FormDataParam("member") String memberJson, @FormDataParam("file") InputStream fileInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileDetail) throws IOException {
        System.out.println(memberJson);
        System.out.println(fileInputStream);
        // Create an ObjectMapper with JavaTimeModule registered
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        JsonFormat.Value format = JsonFormat.Value
                .forPattern("dd-MMM-yy");
        mapper.configOverride(LocalDate.class).setFormat(format);

        // Deserialize in java object
        Member member = mapper.readValue(memberJson, Member.class);
        // Validate the incoming Member object
        Error memberIsValid = entryFormBean.validateMember(member);
        try {
            byte[] fileBytes = fileInputStream.readAllBytes();
            String fileType = fileDetail.getFileName().endsWith(".pdf") ? "application/pdf" : "image/" + fileDetail.getFileName().split("\\.")[1];

            member.setCertificateFile(fileBytes);
            member.setFileType(fileType);
        }catch (Exception e){
            return  Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
        // Call the EntryFormBean to persist the member
        if(memberIsValid.getMessage() == null) {
            entryFormBean.addToPending(member);

            // Return the created member with a success status
            return Response.status(Response.Status.CREATED)
                    .entity(member)
                    .build();
        }else{
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(memberIsValid.getMessage()).build();
        }


    }

}
