package ytuce.gp.mfmsp.Service.ExternalService.Impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.objects.Update;
import ytuce.gp.mfmsp.Constants.AccessTokenName;
import ytuce.gp.mfmsp.Constants.Platform;
import ytuce.gp.mfmsp.Entity.Conversation;
import ytuce.gp.mfmsp.Entity.Message;
import ytuce.gp.mfmsp.Repository.AccessTokenRepository;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Service.ExternalService.ExternalService;

import java.util.*;

@Service
@Log4j2
@AllArgsConstructor
public class TelegramServiceImpl extends TelegramLongPollingBot implements ExternalService {

    @Autowired
    private ConversationRepository conversationRepository;


    @Autowired
    private AccessTokenRepository accessTokenRepository;

    @Override
    public void onUpdateReceived(Update update) {
        // Handle incoming messages or events from Telegram API
    }

    public void sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        Conversation conversation = conversationRepository.getConversationByExternalIdAndPlatform(chatId, Platform.TELEGRAM);
        if(conversation==null){
            conversation=new Conversation();
            conversation.setPlatform(Platform.TELEGRAM);
            conversation.setExternalId(chatId);
            List<Message> messages = new ArrayList<>();
            conversation.setMessages(messages);
        }
        Message message = new Message();
        message.setDirection(true);
        message.setTime(System.currentTimeMillis());
        message.setReadStatus(true);
        message.setText(text);
        conversation.addMessage(message);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            // Handle exception
        }
    }

    public void receiveMessages() {
        GetUpdates request = new GetUpdates();
        request.setLimit(100); // adjust the limit as per your requirement
        request.setOffset(0);
        List<Conversation> conversations = conversationRepository.getConversationsByPlatform(Platform.TELEGRAM);
        HashMap<Long,Conversation> conversationHashMap = new HashMap<>();
        for(Conversation conversation : conversations){
            conversationHashMap.put(Long.valueOf(conversation.getExternalId()),conversation);
        }

        try {
            List<Update> updates = execute(request);
            for (Update update : updates) {
                Conversation conversation = conversationHashMap.get(update.getMessage().getChatId());
                if(conversation==null){
                    conversation = new Conversation();
                    conversation.setPlatform(Platform.TELEGRAM);
                    conversation.setExternalId(String.valueOf(update.getMessage().getChatId()));
                    conversation.setMessages(new ArrayList<>());
                }
                Message message = new Message();
                message.setTime(Long.valueOf(update.getMessage().getDate()));
                message.setText(update.getMessage().getText());
                message.setDirection(false);
                message.setExternalId(String.valueOf(update.getMessage().getMessageId()));
                message.setReadStatus(false);
                if(!conversation.getMessages().contains(message)){
                    conversation.addMessage(message);
                }
                conversationHashMap.put(update.getMessage().getChatId(),conversation);
            }
            List<Conversation> conversationList = conversationHashMap.values().stream().toList();
            for(Conversation conversation:conversationList){
                conversation.getMessages().sort(Comparator.comparing(Message::getTime));
            }
            conversationRepository.saveAll(conversationList);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

    }

    @Override
    public String getBotToken() {
        // Return the bot token provided by Telegram API
        return accessTokenRepository.getByName(AccessTokenName.TELEGRAM_BOT_TOKEN.name()).getValue();
    }

    @Override
    public String getBotUsername() {
        // Return the bot username provided by Telegram API
        return accessTokenRepository.getByName(AccessTokenName.TELEGRAM_BOT_USERNAME.name()).getValue();
    }
}
