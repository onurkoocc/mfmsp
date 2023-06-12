package ytuce.gp.mfmsp.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Representative;
import ytuce.gp.mfmsp.Optaplanner.DistributionService;
import ytuce.gp.mfmsp.Optaplanner.RepresentativeDistribution;
import ytuce.gp.mfmsp.Pojo.ConversationPojo;
import ytuce.gp.mfmsp.Pojo.RepresentativePojo;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Repository.RepresentativeRepository;
import ytuce.gp.mfmsp.Security.auth.RegisterRequest;
import ytuce.gp.mfmsp.Security.user.Role;
import ytuce.gp.mfmsp.Service.ExternalService.ApplicationBridgeService;

import java.util.ArrayList;
import java.util.List;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/test")
@AllArgsConstructor
@EnableAutoConfiguration
@PreAuthorize("hasRole('REPRESENTATIVE')")
public class TestController {
    @Autowired
    ApplicationBridgeService applicationBridgeService;

    @Autowired
    DistributionService distributionService;

    @Autowired
    RepresentativeRepository representativeRepository;

    @Autowired
    ConversationRepository conversationRepository;
    /*deprecated

    @GetMapping("/run")
    public ResponseEntity getById() {

        applicationBridgeService.getAllConversations();
        List<Conversation> conversationList = conversationRepository.findAll();
        List<Conversation> tmpConversationList = new ArrayList<>();
        RepresentativeDistribution solvedDistribution = new RepresentativeDistribution();
        for(Conversation conversation:conversationList){
            tmpConversationList.add(conversation);
            solvedDistribution = distributionService.distribute(representativeRepository.findAll(),tmpConversationList);
        }

        representativeRepository.saveAll(solvedDistribution.getRepresentativeList());
        conversationRepository.saveAll(solvedDistribution.getConversationList());
        return ResponseEntity.ok("distribution completed");
    }
     */


    @GetMapping("/getallconversationsbyrepresentativeid/{representativeid}")
    public ResponseEntity getAllConversations(@PathVariable("representativeid") Integer representativeId) {
        Representative representative = representativeRepository.getReferenceById(representativeId);
        if(representative==null){
            return ResponseEntity.badRequest().body("Representative not found");
        }
        List<ConversationPojo> conversationPojos = RepresentativePojo.entityToPojoBuilder(representative).getConversationList();
        return ResponseEntity.ok(conversationPojos);
    }

    @GetMapping("/getrepresentatives")
    public ResponseEntity getRepresentatives() {
        List<RepresentativePojo> representativePojos = new ArrayList<>();
        List<Representative> representatives = representativeRepository.findAll();
        for(Representative representative:representatives){
            representativePojos.add(RepresentativePojo.entityToPojoBuilder(representative));
        }
        return ResponseEntity.ok(representativePojos);
    }
}
