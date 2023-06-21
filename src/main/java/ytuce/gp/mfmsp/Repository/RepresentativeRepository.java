package ytuce.gp.mfmsp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ytuce.gp.mfmsp.Entity.Representative;

@Repository
public interface RepresentativeRepository extends JpaRepository<Representative, Integer> {
    Representative getByEmail(String email);
}
