package ytuce.gp.mfmsp.Pojo;

import jakarta.persistence.*;
import lombok.Data;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import ytuce.gp.mfmsp.Constants.Platform;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Message;

import java.util.ArrayList;
import java.util.List;
@Data
public class ConversationPojo {
    private Integer id;
    private String externalId;
    private String customerName;
    private Platform platform;
    private List<MessagePojo> messages;
    private Boolean hasEnded;
    private Integer satisfactionRate;
    private Integer duration;

    public static ConversationPojo entityToPojoBuilder(Conversation conversation){
        if(conversation==null){
            return null;
        }
        ConversationPojo conversationPojo = new ConversationPojo();
        conversationPojo.setId(conversation.getId());
        conversationPojo.setExternalId(conversation.getExternalId());
        conversationPojo.setCustomerName(conversation.getCustomerName());
        conversationPojo.setPlatform(conversation.getPlatform());
        conversationPojo.setHasEnded(conversation.getHasEnded());
        conversationPojo.setSatisfactionRate(conversation.getSatisfactionRate());
        conversationPojo.setDuration(conversationPojo.getDuration());
        List<MessagePojo> messagePojos = new ArrayList<>();
        if(conversation.getMessages()!=null){
            for(Message message:conversation.getMessages()){
                messagePojos.add(MessagePojo.entityToPojoBuilder(message));
            }
        }
        conversationPojo.setMessages(messagePojos);
        return conversationPojo;
    }
}
