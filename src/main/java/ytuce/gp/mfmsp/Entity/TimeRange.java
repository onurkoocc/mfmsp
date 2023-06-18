package ytuce.gp.mfmsp.Entity;

import jakarta.persistence.*;
import net.minidev.json.annotate.JsonIgnore;
import ytuce.gp.mfmsp.Pojo.TimeRangePojo;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "timerange")
public class TimeRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_time")
    private LocalDateTime startTime;
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "representative_id")
    private Representative representative;

    public TimeRange() {
    }
    public TimeRange(TimeRangePojo timeRangePojo) {
        this.startTime = timeRangePojo.getStartTime().atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
        this.endTime = timeRangePojo.getEndTime().atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
    }
    public TimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime.atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
        this.endTime = endTime.atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
    }

    public TimeRange(Integer id, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.startTime = startTime.atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
        this.endTime = endTime.atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime.atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
    }


    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime.atZone(ZoneId.of("Europe/Istanbul")).toLocalDateTime();
    }

    public Representative getRepresentative() {
        return representative;
    }

    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }
}
