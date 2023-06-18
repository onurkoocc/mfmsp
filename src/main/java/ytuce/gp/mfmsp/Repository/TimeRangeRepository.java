package ytuce.gp.mfmsp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ytuce.gp.mfmsp.Entity.TimeRange;
@Repository
public interface TimeRangeRepository extends JpaRepository<TimeRange, Integer> {
}
