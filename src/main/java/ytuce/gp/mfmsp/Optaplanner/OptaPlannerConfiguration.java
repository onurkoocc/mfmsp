package ytuce.gp.mfmsp.Optaplanner;

import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ytuce.gp.mfmsp.Entity.Conversation;

import java.util.Collections;

@Configuration
public class OptaPlannerConfiguration {

    @Bean
    public SolverConfig solverConfig() {
        SolverConfig solverConfig = new SolverConfig();
        solverConfig.setSolutionClass(RepresentativeDistribution.class);
        solverConfig.setEntityClassList(Collections.singletonList(Conversation.class));
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        scoreDirectorFactoryConfig.setConstraintProviderClass(RepresentativeDistributionConstraintProvider.class);
        solverConfig.setScoreDirectorFactoryConfig(scoreDirectorFactoryConfig);
        TerminationConfig terminationConfig = new TerminationConfig();
        terminationConfig.setBestScoreLimit("0hard/-1000000soft");
        terminationConfig.setMinutesSpentLimit(10L);
        solverConfig.setTerminationConfig(terminationConfig);

        ConstructionHeuristicType constructionHeuristicType = ConstructionHeuristicType.FIRST_FIT_DECREASING;

        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        constructionHeuristicPhaseConfig.setConstructionHeuristicType(constructionHeuristicType);

        solverConfig.setPhaseConfigList(Collections.singletonList(constructionHeuristicPhaseConfig));

        return solverConfig;
    }
}
