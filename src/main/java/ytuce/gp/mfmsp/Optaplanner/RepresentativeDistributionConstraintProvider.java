package ytuce.gp.mfmsp.Optaplanner;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Representative;

import java.util.function.Function;


public class RepresentativeDistributionConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                balanceConversationWorkload(constraintFactory)
        };
    }

    public Constraint balanceConversationWorkload(ConstraintFactory constraintFactory) {
        return constraintFactory
                .forEach(Representative.class)
                .ifExists(Conversation.class, Joiners.equal(Function.identity(), Conversation::getRepresentative))
                .penalize(
                        HardSoftScore.ONE_SOFT,
                        representative -> representative.getConversationList().stream().mapToInt(Conversation::getMessageCount).sum())
                .asConstraint("Balance conversation workload");
    }

}
