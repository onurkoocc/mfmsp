package ytuce.gp.mfmsp.Pojo;

import lombok.Data;

@Data
public class RepresentativeStatisticsPojo {
    String representativeName;
    Integer ongoingConversationCount;
    Integer endedConversationCount;
    Integer totalConversationCount;
    Integer sentMessageCount;
    Integer receivedMessageCount;
    Integer totalMessageCount;
    Integer sentWordCount;
    Integer receivedWordCount;
    Integer totalWordCount;
    Long workload;
}
