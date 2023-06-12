package ytuce.gp.mfmsp.Optaplanner;

import org.optaplanner.core.api.domain.constraintweight.ConstraintConfigurationProvider;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Representative;

import java.util.List;

@PlanningSolution
public class RepresentativeDistribution {
    @ValueRangeProvider(id = "representativeRange")
    @ProblemFactCollectionProperty
    private List<Representative> representativeList;

    @PlanningEntityCollectionProperty
    private List<Conversation> conversationList;

    @PlanningScore
    private HardSoftScore score;

    public RepresentativeDistribution() {
    }

    public RepresentativeDistribution(List<Representative> representativeList, List<Conversation> conversationList) {
        this.representativeList = representativeList;
        this.conversationList = conversationList;
    }

    public List<Representative> getRepresentativeList() {
        return representativeList;
    }

    public void setRepresentativeList(List<Representative> representativeList) {
        this.representativeList = representativeList;
    }

    public List<Conversation> getConversationList() {
        return conversationList;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "RepresentativeDistribution{" +
                "representativeList=" + representativeList +
                ", conversationList=" + conversationList +
                ", score=" + score +
                '}';
    }
}