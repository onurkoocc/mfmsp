package ytuce.gp.mfmsp.cronjob;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Representative;
import ytuce.gp.mfmsp.Optaplanner.DistributionService;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Repository.RepresentativeRepository;
import ytuce.gp.mfmsp.Service.ExternalService.ApplicationBridgeService;

import java.util.*;

@Component
public class ConversationDistributer {
    @Autowired
    ApplicationBridgeService applicationBridgeService;

    @Autowired
    DistributionService distributionService;

    @Autowired
    RepresentativeRepository representativeRepository;

    @Autowired
    ConversationRepository conversationRepository;
    @Transactional
    @Scheduled(cron = "0 */2 * * * ?")
    public void distribute() {

        long start = System.currentTimeMillis();
        long duration;
        System.out.println("Start : "+start);
        applicationBridgeService.getAllConversations();
        List<Conversation> conversationList = conversationRepository.getAllByRepresentativeIsNull();


        for (Conversation conversation : conversationList) {
            List<Representative> representativeList = representativeRepository.findAll();
            HashMap<Integer, Integer> representativeToWorkloadHashMap = new HashMap<>();
            for (Representative representative : representativeList) {
                if (!representativeToWorkloadHashMap.containsKey(representative.getId())) {
                    representativeToWorkloadHashMap.put(representative.getId(), 0);
                }
                representativeToWorkloadHashMap.put(representative.getId(), representative.getWorkload());
            }
            int minimumWorkload = Integer.MAX_VALUE;
            Integer selectedRepresentativeId = null;
            for(Integer i:representativeToWorkloadHashMap.keySet()){
                if(minimumWorkload>representativeToWorkloadHashMap.get(i)){
                    minimumWorkload=representativeToWorkloadHashMap.get(i);
                    selectedRepresentativeId = i;
                }
            }
            if(selectedRepresentativeId!=null){
                Representative selectedRepresentative = representativeRepository.getReferenceById(selectedRepresentativeId);
                selectedRepresentative.addConversation(conversation);
                conversation.setRepresentative(selectedRepresentative);
                conversationRepository.save(conversation);
                representativeRepository.save(selectedRepresentative);
            }
        }

        List<Representative> representatives = representativeRepository.findAll();
        representatives.forEach(Representative::getWorkload);
        representativeRepository.saveAll(representatives);
        long end=System.currentTimeMillis();
        duration=end-start;
        System.out.println("End : "+ end);
        System.out.println("Duration" + duration);
    }
}
