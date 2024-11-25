package si.fri.prpo.seminarska.entitete;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "certificate")
@NamedQueries(value =
        {
                @NamedQuery(name = "CertificateOfEnrollment.findAll", query = "SELECT c FROM CertificateOfEnrollment c")

        })
public class CertificateOfEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificate_id;

    @Column(name = "university")
    private String university;

    @Column(name = "validFrom")
    private Date validFrom;

    @Column(name = "valitTo")
    private Date validTo;

    @Column(name = "pupilOrStudent")
    private String pupilOrStudent;

    @ManyToOne
    @JoinColumn(name = "member", nullable = false)
    private Member member;

    public Member getMemeber() {
        return member;
    }

    public void setMemeber(Member memeber) {
        this.member = memeber;
    }

    public Long getCertificate_id() {
        return certificate_id;
    }

    public void setCertificate_id(Long certificate_id) {
        this.certificate_id = certificate_id;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public String getPupilOrStudent() {
        return pupilOrStudent;
    }

    public void setPupilOrStudent(String pupilOrStudent) {
        this.pupilOrStudent = pupilOrStudent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CertificateOfEnrollment{\n");
        sb.append("\tcertificate_id=").append(certificate_id).append("\n");
        sb.append("\tuniversity=").append(university).append("\n");
        sb.append("\tvalidFrom=").append(validFrom).append("\n");
        sb.append("\tvalidTo=").append(validTo).append("\n");
        sb.append("\tpupilOrStudent=").append(pupilOrStudent).append("\n");
        sb.append("\tmember=").append(member.getId().toString()).append("\n");
        sb.append("\t}\n");
        return sb.toString();
    }
}
