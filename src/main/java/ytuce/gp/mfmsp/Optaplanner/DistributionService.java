package ytuce.gp.mfmsp.Optaplanner;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.stereotype.Service;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Representative;

import java.util.List;

@Service
public class DistributionService {
    private final SolverFactory<RepresentativeDistribution> solverFactory;

    public DistributionService(SolverConfig solverConfig) {
        this.solverFactory = SolverFactory.create(solverConfig);
    }

    public RepresentativeDistribution distribute(List<Representative> representatives, List<Conversation> conversations) {
        RepresentativeDistribution unsolvedDistribution = new RepresentativeDistribution(representatives, conversations);
        Solver<RepresentativeDistribution> solver = solverFactory.buildSolver();
        return solver.solve(unsolvedDistribution);
    }
}
