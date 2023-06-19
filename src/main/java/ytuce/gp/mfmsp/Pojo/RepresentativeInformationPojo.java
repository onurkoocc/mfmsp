package ytuce.gp.mfmsp.Pojo;

import lombok.Data;
import ytuce.gp.mfmsp.Entity.Representative;
import ytuce.gp.mfmsp.Entity.TimeRange;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Data
public class RepresentativeInformationPojo {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private List<TimeRangePojo> availableWorkHours;
    public RepresentativeInformationPojo(Representative representative){
        this.email = representative.getEmail();
        this.id=representative.getId();
        this.firstname = representative.getFirstname();
        this.lastname = representative.getLastname();
        this.availableWorkHours=new ArrayList<>();
        for(TimeRange timeRange:representative.getAvailableWorkHours()){
            TimeRangePojo timeRangePojo = new TimeRangePojo();
            timeRangePojo.setStartTime(timeRange.getStartTime().atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime());
            timeRangePojo.setEndTime(timeRange.getEndTime().atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime());
            this.availableWorkHours.add(timeRangePojo);
        }
    }
}
