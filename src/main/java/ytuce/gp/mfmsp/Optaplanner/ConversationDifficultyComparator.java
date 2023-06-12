package ytuce.gp.mfmsp.Optaplanner;

import ytuce.gp.mfmsp.Entity.Conversation;

import java.util.Comparator;

public class ConversationDifficultyComparator implements Comparator<Conversation> {
    @Override
    public int compare(Conversation a, Conversation b) {
        // You should implement the comparison logic based on the difficulty of the conversation
        // For example, if "difficulty" is a field in your Conversation class:
        // return a.getDifficulty().compareTo(b.getDifficulty());
        return Integer.compare(a.getMessageCount(), b.getMessageCount());
    }
}
