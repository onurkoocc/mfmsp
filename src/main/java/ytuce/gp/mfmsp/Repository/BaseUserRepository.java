package ytuce.gp.mfmsp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ytuce.gp.mfmsp.Entity.BaseUser;

@Repository
public interface BaseUserRepository extends JpaRepository<BaseUser, Integer> {
}
