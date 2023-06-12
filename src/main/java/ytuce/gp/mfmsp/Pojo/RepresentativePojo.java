package ytuce.gp.mfmsp.Pojo;

import lombok.Data;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Representative;

import java.util.ArrayList;
import java.util.List;
@Data
public class RepresentativePojo {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private List<ConversationPojo> conversationList;
    private Integer successRate;

    private Integer workload;

    public static RepresentativePojo entityToPojoBuilder(Representative representative){
        if(representative==null){
            return null;
        }
        RepresentativePojo representativePojo = new RepresentativePojo();
        representativePojo.setId(representative.getId());
        representativePojo.setFirstname(representative.getFirstname());
        representativePojo.setLastname(representative.getLastname());
        representativePojo.setEmail(representative.getEmail());
        representativePojo.setSuccessRate(representative.getSuccessRate());
        representativePojo.setWorkload(representative.getWorkload());
        List<ConversationPojo> conversationPojos = new ArrayList<>();
        if(representative.getConversationList()!=null){
            for(Conversation conversation:representative.getConversationList()){
                conversationPojos.add(ConversationPojo.entityToPojoBuilder(conversation));
            }
        }
        representativePojo.setConversationList(conversationPojos);
        return representativePojo;
    }
}
