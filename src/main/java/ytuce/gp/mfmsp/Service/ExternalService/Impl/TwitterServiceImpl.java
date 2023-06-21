package ytuce.gp.mfmsp.Service.ExternalService.Impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.v1.DirectMessage;
import twitter4j.v1.ResponseList;
import ytuce.gp.mfmsp.Constants.AccessTokenName;
import ytuce.gp.mfmsp.Constants.Platform;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Message;
import ytuce.gp.mfmsp.Repository.AccessTokenRepository;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Service.ExternalService.ExternalService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@AllArgsConstructor
public class TwitterServiceImpl implements ExternalService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    private static final long ACCOUNT_ID = 927322358095982593L;


    public void connect() {


    }

    private Twitter twitterBuilder() {
        String CONSUMER_KEY = accessTokenRepository.getByName(AccessTokenName.TWITTER_CONSUMER_KEY.name()).getValue();
        String CONSUMER_SECRET = accessTokenRepository.getByName(AccessTokenName.TWITTER_CONSUMER_SECRET.name()).getValue();
        String ACCESS_TOKEN = accessTokenRepository.getByName(AccessTokenName.TWITTER_ACCESS_TOKEN.name()).getValue();
        String ACCESS_TOKEN_SECRET = accessTokenRepository.getByName(AccessTokenName.TWITTER_ACCESS_TOKEN_SECRET.name()).getValue();
        return Twitter.newBuilder().prettyDebugEnabled(true).oAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET).
                oAuthAccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET).build();
    }

    public void sendMessage(String recipient, String messageText) {
        Twitter twitter = twitterBuilder();
        try {
            twitter.v1().directMessages().sendDirectMessage(Long.parseLong(recipient), messageText);
        } catch (TwitterException e) {
            System.out.println(e.getMessage());
        }

    }

    public void receiveMessages() {
        try {
            Twitter twitter = twitterBuilder();
            ResponseList<DirectMessage> messages = twitter.v1().directMessages().getDirectMessages(100);
            conversationMapper(messages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Conversation> conversationMapper(ResponseList<DirectMessage> messages) {
        Map<Long, Conversation> conversationMap = new HashMap<>();
        List<Conversation> conversations = conversationRepository.getConversationsByPlatform(Platform.TWITTER);
        for (Conversation conversation : conversations) {
            conversationMap.put(Long.valueOf(conversation.getExternalId()), conversation);
        }

        for (DirectMessage externalMessage : messages) {
            long mapId = externalMessage.getRecipientId() != ACCOUNT_ID
                    ? externalMessage.getRecipientId()
                    : externalMessage.getSenderId();

            Conversation conversation = conversationMap.get(mapId);

            if (conversation == null) {
                conversation = new Conversation();
                conversation.setHasEnded(false);
                conversation.setPlatform(Platform.TWITTER);
                conversation.setExternalId(String.valueOf(mapId));
            }

            Message message = new Message();
            ZonedDateTime zonedCreatedAt = externalMessage.getCreatedAt().atZone(ZoneId.systemDefault());
            message.setTime(zonedCreatedAt.toInstant().toEpochMilli());
            message.setText(externalMessage.getText());
            message.setExternalId(String.valueOf(externalMessage.getId()));
            message.setDirection(externalMessage.getSenderId() == ACCOUNT_ID);


            conversation.addMessage(message);
            conversationMap.put(mapId, conversation);
        }

        for (Conversation conversation : conversationMap.values()) {
            List<Message> messageList = conversation.getMessages();
            messageList.sort(Comparator.comparing(Message::getTime));
        }

        return conversationRepository.saveAll(conversationMap.values());
    }
}
