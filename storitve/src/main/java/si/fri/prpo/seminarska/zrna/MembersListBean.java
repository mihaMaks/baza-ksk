package si.fri.prpo.seminarska.zrna;

import org.glassfish.jersey.internal.Errors;
import si.fri.prpo.seminarska.entitete.CertificateOfEnrollment;
import si.fri.prpo.seminarska.entitete.Event;
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
        return em.createNamedQuery("Members.getAll", Member.class).getResultList();
    }

    public List<Member> getPaginatedMembers(int offset, int limit) {
        return em.createQuery("SELECT m FROM Member m", Member.class)
                .setFirstResult(offset) // Start position
                .setMaxResults(limit)   // Maximum number of results
                .getResultList();
    }

    public long getTotalMemberCount() {
        return em.createQuery("SELECT COUNT(m) FROM Member m", Long.class).getSingleResult();
    }


    public List<Member>getPendingMembersList(){
        return em.createNamedQuery("Members.getPending", Member.class).getResultList();
    }

    public Member getMemberById(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> getExistingMembers(Member member){

        String pname = member.getName(); String psurname = member.getSurname();
        List<Member> existingMembers = em.createNamedQuery("Members.findByNameAndSurname", Member.class)
                .setParameter("name", "%" + pname + "%")
                .setParameter("surname", "%" + psurname + "%")
                .setParameter("pending", false)
                .getResultList();
        return existingMembers;

    }


    public boolean deleteMember(Long id) {
        Member member = getMemberById(id);
        if (member != null) {
            em.remove(member);
            return true;
        }
        return false;
    }


    public Member updateMember(Long id, Member member) {
        Member existingMember = getMemberById(id);
        if (existingMember == null) {
            return null;
        }

        em.merge(member);
        return member;
    }
    public Member addCertificateOfEnrollment(Member member, CertificateOfEnrollment certificate){
        try{
            // Associate the certificate with the member
            certificate.setMemeber(member);

            // Add the certificate to the member's list of enrollments
            List<CertificateOfEnrollment> enrollments = member.getEnrollments();
            enrollments.add(certificate);
            member.setEnrollments(enrollments);

            // Handle staus and pending
            member.setStatus(true);
            member.setPending(false);

            em.persist(certificate);
            return em.merge(member);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
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
            System.out.println(errorMessage);
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

    public List<Event> getEvents(long id){
        return em.createNamedQuery("Members.getEventsForMember", Event.class)
                .setParameter("memberId", id)
                .getResultList();
    }


}
