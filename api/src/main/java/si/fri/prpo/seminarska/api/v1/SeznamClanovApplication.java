package si.fri.prpo.seminarska.api.v1;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import si.fri.prpo.seminarska.api.v1.viri.CORSFilter;

import javax.ws.rs.ApplicationPath;



@ApplicationPath("v1")
public class SeznamClanovApplication extends ResourceConfig {
    public SeznamClanovApplication() {

        packages("si.fri.prpo.seminarska.api.v1"); // Adjust the package name to your project
        register(MultiPartFeature.class);  // Register Multipart support
        register(CORSFilter.class); // Register the CORS filter
    }

}
