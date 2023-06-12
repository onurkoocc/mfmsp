package ytuce.gp.mfmsp.Entity;

import jakarta.persistence.*;
import net.minidev.json.annotate.JsonIgnore;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import ytuce.gp.mfmsp.Constants.Platform;
import ytuce.gp.mfmsp.Optaplanner.ConversationDifficultyComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "conversation")
@PlanningEntity(difficultyComparatorClass = ConversationDifficultyComparator.class)
public class Conversation {
    @Id
    @PlanningId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String externalId;
    private String customerName;
    private Platform platform;
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages;
    private Boolean hasEnded;
    private Integer satisfactionRate;
    private Integer duration;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "representative_id")
    @PlanningVariable(valueRangeProviderRefs = "representativeRange")
    private Representative representative;

    public int calculateWorkLoad() {
        if(messages==null){
            messages=new ArrayList<>();
        }
        return messages.size();
    }
    public int getMessageCount() {
        if(messages==null){
            messages=new ArrayList<>();
        }
        return messages.size();
    }

    public void addMessage(Message message) {
        if(this.messages==null){
            this.messages=new ArrayList<>();
        }
        if(!this.messages.contains(message)){
            this.messages.add(message);
            message.setConversation(this);
        }
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setConversation(null);
    }

    public Representative getRepresentative() {
        return representative;
    }

    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public List<Message> getMessages() {
        if(messages==null){
            return new ArrayList<>();
        }
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Boolean getHasEnded() {
        return hasEnded;
    }

    public void setHasEnded(Boolean hasEnded) {
        this.hasEnded = hasEnded;
    }

    public Integer getSatisfactionRate() {
        return satisfactionRate;
    }

    public void setSatisfactionRate(Integer satisfactionRate) {
        this.satisfactionRate = satisfactionRate;
    }

    public Integer getDuration() {
        return messages.size();
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", externalId='" + externalId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", platform=" + platform +
                ", messages=" + messages +
                ", hasEnded=" + hasEnded +
                ", satisfactionRate=" + satisfactionRate +
                ", duration=" + duration +
                ", representative=" + (representative!=null?representative.getId().toString():"null")+
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return Objects.equals(externalId, that.externalId) && platform == that.platform && Objects.equals(messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, platform, messages);
    }
}
