package ytuce.gp.mfmsp.Optaplanner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Representative;

import java.util.List;

@RestController
@RequestMapping("/distribute")
public class DistributionController {
    private final DistributionService distributionService;

    public DistributionController(DistributionService distributionService) {
        this.distributionService = distributionService;
    }

    @PostMapping
    public ResponseEntity<RepresentativeDistribution> distributeConversations(
            @RequestBody List<Representative> representatives,
            @RequestBody List<Conversation> conversations) {
        RepresentativeDistribution solvedDistribution = distributionService.distribute(representatives, conversations);
        return ResponseEntity.ok(solvedDistribution);
    }
}
