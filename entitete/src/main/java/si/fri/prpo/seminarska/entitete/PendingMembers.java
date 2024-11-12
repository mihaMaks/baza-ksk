package si.fri.prpo.seminarska.entitete;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pending-members")
@NamedQueries(value =
        {
                @NamedQuery(name = "PendingMembers.getAll", query = "SELECT n FROM Member n"),
                @NamedQuery(name = "PendingMembers.getAllForUser",
                        query = "SELECT n FROM Member n WHERE n.id = :id")
        })

public class PendingMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Surname is required")
    @Column(name = "surname")
    private String surname;

    @Column(name = "dateOfBirth")
    private Date dateOfBirth;

    @NotBlank(message = "Home address is required")
    @Column(name = "homeAddress")
    private String homeAddress;

    @NotBlank(message = "City is required")
    @Column(name = "city")
    private String city;

    @Pattern(regexp = "\\d{5}", message = "ZIP code must be 5 digits")
    @Column(name = "zipCode")
    private String zipCode;

    @Email(message = "Email should be valid")
    @Column(name = "email")
    private String email;

    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Phone number is invalid")
    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "status")
    private Boolean status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Name is required") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") String name) {
        this.name = name;
    }

    public @NotBlank(message = "Surname is required") String getSurname() {
        return surname;
    }

    public void setSurname(@NotBlank(message = "Surname is required") String surname) {
        this.surname = surname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public @NotBlank(message = "Home address is required") String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(@NotBlank(message = "Home address is required") String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public @NotBlank(message = "City is required") String getCity() {
        return city;
    }

    public void setCity(@NotBlank(message = "City is required") String city) {
        this.city = city;
    }

    public @Pattern(regexp = "\\d{5}", message = "ZIP code must be 5 digits") String getZipCode() {
        return zipCode;
    }

    public void setZipCode(@Pattern(regexp = "\\d{5}", message = "ZIP code must be 5 digits") String zipCode) {
        this.zipCode = zipCode;
    }

    public @Email(message = "Email should be valid") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "Email should be valid") String email) {
        this.email = email;
    }

    public @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Phone number is invalid") String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Pattern(regexp = "\\+?[0-9]{10,15}", message = "Phone number is invalid") String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public Boolean getStatus() {
        return status;
    }
    public void setStatus(Boolean status) {
        this.status = status;
    }
}

