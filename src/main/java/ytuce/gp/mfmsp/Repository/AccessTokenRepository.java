package ytuce.gp.mfmsp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ytuce.gp.mfmsp.Constants.AccessTokenName;
import ytuce.gp.mfmsp.Entity.AccessToken;

public interface AccessTokenRepository extends JpaRepository<AccessToken, Integer> {
    AccessToken getByName(String name);
}
