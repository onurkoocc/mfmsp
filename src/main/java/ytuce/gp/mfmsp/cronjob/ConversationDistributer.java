package ytuce.gp.mfmsp.cronjob;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Message;
import ytuce.gp.mfmsp.Entity.Representative;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Repository.RepresentativeRepository;
import ytuce.gp.mfmsp.Service.ExternalService.ApplicationBridgeService;

import java.util.HashMap;
import java.util.List;

@Component
public class ConversationDistributer {
    @Autowired
    ApplicationBridgeService applicationBridgeService;

    @Autowired
    RepresentativeRepository representativeRepository;

    @Autowired
    ConversationRepository conversationRepository;
    @Transactional
    @Scheduled(cron = "0 */2 * * * ?")
    public void distribute() {

        long start = System.currentTimeMillis();
        applicationBridgeService.getAllConversations();
        List<Conversation> conversationList = conversationRepository.getAllByRepresentativeIsNull();

    //
        for (Conversation conversation : conversationList) {
            List<Representative> representativeList = representativeRepository.findAll();
            Integer selectedRepresentativeId = selectLeastWorkedRepresentative(representativeList);
            if(selectedRepresentativeId!=null){
                Representative selectedRepresentative = representativeRepository.getReferenceById(selectedRepresentativeId);
                selectedRepresentative.addConversation(conversation);
                conversation.setRepresentative(selectedRepresentative);
                conversation.setHasEnded(false);
                conversationRepository.save(conversation);
                representativeRepository.save(selectedRepresentative);
            }
        }
        long end=System.currentTimeMillis();
        System.out.println("Distribution Duration" + (end-start));
    }

    private Integer selectLeastWorkedRepresentative(List<Representative> representativeList){
        HashMap<Integer, Long> representativeToWorkloadHashMap = new HashMap<>();
        for (Representative representative : representativeList) {
            if (!representativeToWorkloadHashMap.containsKey(representative.getId())) {
                representativeToWorkloadHashMap.put(representative.getId(), 0L);
            }
            representativeToWorkloadHashMap.put(representative.getId(), representative.getWorkload());
        }
        long minimumWorkload = Long.MAX_VALUE;
        Integer selectedRepresentativeId = null;
        for(Integer i:representativeToWorkloadHashMap.keySet()){
            Representative representative = representativeRepository.getReferenceById(i);
            if(minimumWorkload>representativeToWorkloadHashMap.get(i) && representative.isAvailableToWork()){
                minimumWorkload=representativeToWorkloadHashMap.get(i);
                selectedRepresentativeId = i;
            }
        }
        return selectedRepresentativeId;
    }
    @Transactional
    @Scheduled(cron = "0 */5 * * * ?")
    public void reassign() {
        long start = System.currentTimeMillis();
        List<Conversation> conversations = conversationRepository.getAllByHasEndedFalseOrHasEndedNull();
        for(Conversation conversation:conversations){
            if(conversation==null){
                continue;
            }
            if(conversation.getMessages()==null || conversation.getMessages().isEmpty()){
                continue;
            }
            Message lastMessage = conversation.getMessages().get(conversation.getMessages().size()-1);
            if(!lastMessage.getDirection()&&(System.currentTimeMillis()-lastMessage.getTime())>1000*60*30){
                //conversation.getRepresentative().getConversationList().remove(conversation);
                List<Representative> representativeList = representativeRepository.findAll();
                representativeList.remove(conversation.getRepresentative());
                Integer selectedRepresentativeId=selectLeastWorkedRepresentative(representativeList);
                if(selectedRepresentativeId!=null){
                    Representative newRepresentative = representativeRepository.getReferenceById(selectedRepresentativeId);
                    conversation.setRepresentative(newRepresentative);
                    conversationRepository.save(conversation);
                }
            }
        }
        long end=System.currentTimeMillis();
        System.out.println("Reassign Duration : "+ (end-start));
    }

    @Transactional
    @Scheduled(cron = "0/10 * * * * ?")
    public void workloadUpdater() {
        long start = System.currentTimeMillis();
        System.out.println("Workload Updater Start : "+start);
        List<Representative> representatives = representativeRepository.findAll();
        representatives.forEach(Representative::getWorkload);
        representativeRepository.saveAll(representatives);
        long end=System.currentTimeMillis();
        System.out.println("Workload Updater Duration : "+ (end-start));
    }

}
