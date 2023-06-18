package ytuce.gp.mfmsp.Pojo;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeRangePojo {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
