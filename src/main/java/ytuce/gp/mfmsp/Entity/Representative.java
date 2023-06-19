package ytuce.gp.mfmsp.Entity;

import jakarta.persistence.*;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "representative")
public class Representative extends BaseUser {

    @OneToMany(mappedBy = "representative", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Conversation> conversationList;
    private Integer successRate;

    private Long workload;

    @OneToMany(mappedBy = "representative", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TimeRange> availableWorkHours;

    public Long getWorkload() {
        this.workload=calculateWorkLoad();
        return workload;
    }

    private Long calculateWorkLoad(){
        if(conversationList == null){
            conversationList=new ArrayList<>();
        }
        return conversationList.stream().mapToLong(Conversation::calculateWorkLoad).sum()+conversationList.size()* 3L;
    }
    private void setWorkload(Long workload) {
        this.workload = workload;
    }

    public Representative() {
    }

    public Representative(String firstname, String lastname, String email, String password) {
        super(firstname, lastname, email, password);
    }

    public void addConversation(Conversation conversation) {
        if(this.conversationList==null){
            this.conversationList=new ArrayList<>();
        }
        if(!this.conversationList.contains(conversation)){
            this.conversationList.add(conversation);
            conversation.setRepresentative(this);
        }
    }
    public void removeConversation(Conversation conversation) {
        conversationList.remove(conversation);
        conversation.setRepresentative(null);
    }

    public List<Conversation> getConversationList() {
        if(conversationList==null){
            conversationList = new ArrayList<>();
        }
        return conversationList;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    public Integer getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Integer successRate) {
        this.successRate = successRate;
    }

    public List<TimeRange> getAvailableWorkHours() {
        if(this.availableWorkHours==null){
            this.availableWorkHours = new ArrayList<>();
        }
        return availableWorkHours;
    }

    public void setAvailableWorkHours(List<TimeRange> availableWorkHours) {
        this.availableWorkHours = availableWorkHours;
    }

    public void addTimeRangeToAvailableWorkHours(TimeRange timeRange) {
        if(this.availableWorkHours==null){
            this.availableWorkHours=new ArrayList<>();
        }
        availableWorkHours.add(timeRange);
    }

    public boolean isAvailableToWork(){
        List<TimeRange> timeRanges = getAvailableWorkHours();
        ZonedDateTime nowTurkeyTime = ZonedDateTime.now(ZoneId.of("Europe/Istanbul"));
        return timeRanges.stream()
                .anyMatch(t->nowTurkeyTime.isAfter(ZonedDateTime.of(t.getStartTime(), ZoneId.of("Europe/Istanbul")))
                &&nowTurkeyTime.isBefore(ZonedDateTime.of(t.getEndTime(), ZoneId.of("Europe/Istanbul"))));

    }

    @Override
    public String toString() {
        return "Representative{" +
                "conversationList=" + conversationList +
                ", successRate=" + successRate +
                '}';
    }
}
