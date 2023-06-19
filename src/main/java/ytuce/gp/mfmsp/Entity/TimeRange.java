package ytuce.gp.mfmsp.Entity;

import jakarta.persistence.*;
import net.minidev.json.annotate.JsonIgnore;
import ytuce.gp.mfmsp.Pojo.TimeRangePojo;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "time_range")
public class TimeRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_time")
    private String startTime;
    @Column(name="end_time")
    private String endTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "representative_id")
    private Representative representative;

    public TimeRange() {
    }
    public TimeRange(TimeRangePojo timeRangePojo) {
        this.startTime = timeRangePojo.getStartTime();
        this.endTime = timeRangePojo.getEndTime();
    }
    public TimeRange(OffsetDateTime startTime, OffsetDateTime endTime) {
        this.startTime = startTime.toString();
        this.endTime = endTime.toString();
    }

    public TimeRange(Integer id, OffsetDateTime startTime, OffsetDateTime endTime) {
        this.id = id;
        this.startTime = startTime.toString();
        this.endTime = endTime.toString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Representative getRepresentative() {
        return representative;
    }

    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }
}
