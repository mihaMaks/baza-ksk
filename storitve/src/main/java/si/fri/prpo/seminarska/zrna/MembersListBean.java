package si.fri.prpo.seminarska.zrna;

import org.glassfish.jersey.internal.Errors;
import si.fri.prpo.seminarska.entitete.Member;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
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

    public List<Member>getPendingMembersList(){
        List<Member> members = em.createNamedQuery("Members.getPending", Member.class).getResultList();
        return members;
    }

    public Member getMemberById(Long id){
        List<Member> members = em.createNamedQuery("Members.getAllForId", Member.class)
                .setParameter("id", id)
                .getResultList();
        return members.get(0);
    }

    public Error validateMember(Member member){
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors: ");
            for (ConstraintViolation<Member> violation : violations) {
                System.out.println(violation.getMessage());
                errorMessage.append(violation.getMessage()).append("\n");
            }
            System.out.println(errorMessage.toString());
            return new Error(errorMessage.toString());
        }
        return new Error();
    }

    @Transactional
    public void addToPending(Member member){
        try {
            member.setPending(true);
            em.persist(member);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
