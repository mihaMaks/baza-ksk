package si.fri.prpo.seminarska.zrna;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.logging.Logger;

import com.kumuluz.ee.rest.beans.QueryParameters;
//import com.kumuluz.ee.rest.utils.JPAUtils;

import si.fri.prpo.seminarska.entitete.Uporabnik;

@ApplicationScoped
public class UporabnikiZrno {

    private Logger log = Logger.getLogger(UporabnikiZrno.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija zrna " + UporabnikiZrno.class.getSimpleName());

        // inicializacija virov
    }

    @PreDestroy
    private void destroy() {
        log.info("Deinicializacija zrna " + UporabnikiZrno.class.getSimpleName());

        // zapiranje virov
    }

    @PersistenceContext(unitName = "nakupovalni-seznami-jpa")
    private EntityManager em;

    public List<Uporabnik> pridobiUporabnike() {

        // TODO: missing implementation

        return null;

    }

    public List<Uporabnik> pridobiUporabnike(QueryParameters query) {
        
        // TODO: missing implementation

        return null;

    }

    public Long pridobiUporabnikeCount(QueryParameters query) {
        
        // TODO: missing implementation

        return null;

    }

    public List<Uporabnik> pridobiUporabnikeCriteriaAPI() {

        // TODO: missing implementation

        return null;

    }

    public Uporabnik pridobiUporabnika(int uporabnikId) {

        // TODO: missing implementation

        return null;

    }

    @Transactional
    public Uporabnik dodajUporabnika(Uporabnik uporabnik) {

        // TODO: missing implementation

        return null;

    }

    @Transactional
    public Uporabnik posodobiUporabnika(int uporabnikId, Uporabnik uporabnik) {

        // TODO: missing implementation

        return null;

    }

    @Transactional
    public boolean odstraniUporanbika(int uporabnikId) {

        // TODO: missing implementation

        return false;

    }
}
