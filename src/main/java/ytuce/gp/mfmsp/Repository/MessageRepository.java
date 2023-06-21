package ytuce.gp.mfmsp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ytuce.gp.mfmsp.Entity.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

}
