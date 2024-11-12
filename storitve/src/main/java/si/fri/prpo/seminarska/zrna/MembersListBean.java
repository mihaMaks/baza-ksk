package si.fri.prpo.seminarska.zrna;

import si.fri.prpo.seminarska.entitete.Member;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

@ApplicationScoped
public class MembersListBean {
    private Logger log = Logger.getLogger(MembersListBean.class.getName());

    @PostConstruct
    private void init() {
        log.info("Inicializacija zrna " + MembersListBean.class.getSimpleName());

        // inicializacija virov
    }

    @PreDestroy
    private void destroy() {
        log.info("Deinicializacija zrna " + MembersListBean.class.getSimpleName());

        // zapiranje virov
    }
    @PersistenceContext(unitName = "seznam-clanov-jpa")
    private EntityManager em;

    public List<Member>getMemberList(){
        List<Member> members = em.createNamedQuery("Members.getAll", Member.class).getResultList();
        return members;
    }

    public Member getMemberById(Long id){
        List<Member> members = em.createNamedQuery("Members.getAllForId", Member.class)
                .setParameter("id", id)
                .getResultList();
        return members.get(0);
    }

}
