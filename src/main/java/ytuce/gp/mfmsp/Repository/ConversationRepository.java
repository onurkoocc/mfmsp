package ytuce.gp.mfmsp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ytuce.gp.mfmsp.Constants.Platform;
import ytuce.gp.mfmsp.Entity.Conversation;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    Boolean existsByExternalId(String externalId);
    Conversation getConversationByExternalId(String externalId);

    Conversation getConversationByExternalIdAndPlatform(String externalId, Platform platform);
    List<Conversation> getConversationsByPlatform(Platform platform);

    List<Conversation> getAllByRepresentativeIsNotNull();

    @Query("SELECT c FROM Conversation c JOIN FETCH c.messages WHERE c.representative IS NULL")
    List<Conversation> getAllByRepresentativeIsNull();

    List<Conversation> getConversationsByRepresentative_Id(Integer id);
}
