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
import ytuce.gp.mfmsp.Entity.TimeRange;
import ytuce.gp.mfmsp.Pojo.*;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Repository.RepresentativeRepository;
import ytuce.gp.mfmsp.Repository.TimeRangeRepository;
import ytuce.gp.mfmsp.Service.ExternalService.ApplicationBridgeService;
import ytuce.gp.mfmsp.Service.ExternalService.ExternalService;

import java.util.Comparator;
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
    RepresentativeRepository representativeRepository;

    @Autowired
    ConversationRepository conversationRepository;

    @Autowired
    TimeRangeRepository timeRangeRepository;


    @GetMapping("/getallconversationsoftherepresentative")
    public ResponseEntity getAllConversations() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Representative representative = representativeRepository.getByEmail(userDetails.getUsername());
        if (representative == null) {
            return ResponseEntity.badRequest().body("Representative not found");
        }
        List<ConversationPojo> conversationPojos =
                RepresentativePojo.entityToPojoBuilder(representative).getConversationList().stream()
                        .filter(c -> !c.getHasEnded())
                        .filter(c -> !c.getMessages().isEmpty())
                        .sorted(Comparator.comparing(c -> c.getMessages().get(c.getMessages().size() - 1).getTime()))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(conversationPojos);
    }

    @GetMapping("/getallendedconversationsoftherepresentative")
    public ResponseEntity getAllEndedConversations() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Representative representative = representativeRepository.getByEmail(userDetails.getUsername());
        if (representative == null) {
            return ResponseEntity.badRequest().body("Representative not found");
        }
        List<ConversationPojo> conversationPojos =
                RepresentativePojo.entityToPojoBuilder(representative).getConversationList().stream()
                        .filter(ConversationPojo::getHasEnded)
                        .filter(c -> !c.getMessages().isEmpty())
                        .sorted(Comparator.comparing(c -> c.getMessages().get(c.getMessages().size() - 1).getTime()))
                        .collect(Collectors.toList());
        return ResponseEntity.ok(conversationPojos);
    }


    @GetMapping("/getrepresentativeinformationbytoken")
    public ResponseEntity getRepresentativeInformation() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Representative representative = representativeRepository.getByEmail(userDetails.getUsername());
        if (representative == null) {
            return ResponseEntity.badRequest().body("Representative not found");
        }
        return ResponseEntity.ok(new RepresentativeInformationPojo(representative));
    }

    @PostMapping("/addtimerangetorepresentative")
    public ResponseEntity addTimeRangeToRepresentative(
            @RequestBody TimeRangePojo timeRangePojo
    ) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Representative representative = representativeRepository.getByEmail(userDetails.getUsername());
        if (representative == null) {
            return ResponseEntity.badRequest().body("Representative not found");
        }
        TimeRange timeRange = new TimeRange(timeRangePojo);
        timeRange.setRepresentative(representative);
        representative.addTimeRangeToAvailableWorkHours(timeRange);
        timeRangeRepository.save(timeRange);
        representativeRepository.save(representative);
        return ResponseEntity.ok("ok");
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
