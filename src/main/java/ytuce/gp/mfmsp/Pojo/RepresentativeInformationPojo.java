package ytuce.gp.mfmsp.Pojo;

import lombok.Data;
import ytuce.gp.mfmsp.Entity.Representative;
import ytuce.gp.mfmsp.Entity.TimeRange;

import java.util.List;

@Data
public class RepresentativeInformationPojo {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private List<TimeRange> availableWorkHours;
    public RepresentativeInformationPojo(Representative representative){
        this.email = representative.getEmail();
        this.id=representative.getId();
        this.firstname = representative.getFirstname();
        this.lastname = representative.getLastname();
        this.availableWorkHours = representative.getAvailableWorkHours();
    }
}
