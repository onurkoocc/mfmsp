package ytuce.gp.mfmsp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ytuce.gp.mfmsp.Entity.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
}
