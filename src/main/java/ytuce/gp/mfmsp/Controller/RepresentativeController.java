package ytuce.gp.mfmsp.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Representative;
import ytuce.gp.mfmsp.Optaplanner.DistributionService;
import ytuce.gp.mfmsp.Pojo.ConversationPojo;
import ytuce.gp.mfmsp.Pojo.RepresentativePojo;
import ytuce.gp.mfmsp.Pojo.SendMessagePojo;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Repository.RepresentativeRepository;
import ytuce.gp.mfmsp.Service.ExternalService.ApplicationBridgeService;
import ytuce.gp.mfmsp.Service.ExternalService.ExternalService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/representative")
@AllArgsConstructor
@EnableAutoConfiguration
@PreAuthorize("hasRole('REPRESENTATIVE')")
public class RepresentativeController {
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


    @GetMapping("/getallconversationsoftherepresentative")
    public ResponseEntity getAllConversations() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Representative representative = representativeRepository.getByEmail(userDetails.getUsername());
        if (representative == null) {
            return ResponseEntity.badRequest().body("Representative not found");
        }
        List<ConversationPojo> conversationPojos = RepresentativePojo.entityToPojoBuilder(representative).getConversationList()
                .stream().filter(c -> !c.getHasEnded()).collect(Collectors.toList());
        return ResponseEntity.ok(conversationPojos);
    }

    @GetMapping("/getallendedconversationsoftherepresentative")
    public ResponseEntity getAllEndedConversations() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Representative representative = representativeRepository.getByEmail(userDetails.getUsername());
        if (representative == null) {
            return ResponseEntity.badRequest().body("Representative not found");
        }
        List<ConversationPojo> conversationPojos = RepresentativePojo.entityToPojoBuilder(representative).getConversationList()
                .stream().filter(ConversationPojo::getHasEnded).collect(Collectors.toList());
        return ResponseEntity.ok(conversationPojos);
    }


    @PostMapping("/sendmessagebyconversationid")
    public ResponseEntity sendMessageByConversationId(
            @RequestBody SendMessagePojo sendMessagePojo) {
        if (sendMessagePojo == null) {
            return ResponseEntity.badRequest().body("Request data must be not null");
        }
        Optional<Conversation> optConversation = conversationRepository.findById(sendMessagePojo.getId());
        if (optConversation.isEmpty()) {
            return ResponseEntity.badRequest().body("Conversation not found by this id");
        }
        Conversation conversation = optConversation.get();
        ExternalService externalService = applicationBridgeService.getExternalServiceByPlatformName(conversation.getPlatform());
        if (externalService == null) {
            return ResponseEntity.badRequest().body("Platform service not found");
        }
        externalService.sendMessage(conversation.getExternalId(), sendMessagePojo.getText());
        return ResponseEntity.ok("Message send successful");
    }

    @PostMapping("/endconversationbyid/{id}")
    public ResponseEntity endConversationById(@PathVariable("id") Integer id) {
        Optional<Conversation> optConversation = conversationRepository.findById(id);
        if (optConversation.isEmpty()) {
            return ResponseEntity.badRequest().body("Conversation not found");
        }
        Conversation conversation = optConversation.get();
        conversation.setHasEnded(true);
        conversationRepository.save(conversation);
        return ResponseEntity.ok("Conversation ended");
    }


}
