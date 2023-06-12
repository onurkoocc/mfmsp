package ytuce.gp.mfmsp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ytuce.gp.mfmsp.Entity.Configuration;
@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration,String> {
    Configuration findByKey(String key);
}
