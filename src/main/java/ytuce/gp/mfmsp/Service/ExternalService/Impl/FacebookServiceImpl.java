package ytuce.gp.mfmsp.Service.ExternalService.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ytuce.gp.mfmsp.Constants.AccessTokenName;
import ytuce.gp.mfmsp.Constants.Platform;
import ytuce.gp.mfmsp.Repository.AccessTokenRepository;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Service.ExternalService.ExternalService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
@AllArgsConstructor
public class FacebookServiceImpl implements ExternalService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    private static final String pageId = "103158119328523";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    public void sendMessage(String recipientId, String messageText) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, String> recipient = new HashMap<>();
            recipient.put("id", recipientId);

            Map<String, String> message = new HashMap<>();
            message.put("text", messageText);

            String accessToken = accessTokenRepository.getByName(AccessTokenName.META_ACCESS_TOKEN.name()).getValue();


            String url = "https://graph.facebook.com/v15.0/103158119328523/messages?" +
                    "recipient=" + URLEncoder.encode(mapper.writeValueAsString(recipient), StandardCharsets.UTF_8) +
                    "&messaging_type=RESPONSE" +
                    "&message=" + URLEncoder.encode(mapper.writeValueAsString(message), StandardCharsets.UTF_8) +
                    "&access_token=" + accessToken;

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();


            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            os.write(url.getBytes(StandardCharsets.UTF_8));
            os.close();


            System.out.println("Response Code : " + conn.getResponseCode());
            System.out.println("Response Message : " + conn.getResponseMessage());

            if (conn.getResponseCode() != 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String output;
                System.out.println("Error from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receiveMessages() {
        try {
            String accessToken = accessTokenRepository.getByName(AccessTokenName.META_ACCESS_TOKEN.name()).getValue();

            String url = "https://graph.facebook.com/v15.0/103158119328523/conversations?" +
                    "fields=messages%7Bmessage%2Cid%2Cto%2Cfrom%2Ccreated_time%7D" +
                    "&platform=MESSENGER" +
                    "&access_token=" + accessToken;

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();


            conn.setRequestMethod("GET");


            System.out.println("Response Code : " + conn.getResponseCode());
            System.out.println("Response Message : " + conn.getResponseMessage());


            if (conn.getResponseCode() != 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String output;
                System.out.println("Error from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String output;
                System.out.println("Response from Server .... \n");
                while ((output = br.readLine()) != null) {
                    result.append(output);
                }

                Gson gson = new Gson();

                Root root = gson.fromJson(result.toString(), Root.class);

                conversationMapper(root.data);
            }

            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private List<ytuce.gp.mfmsp.Entity.Conversation> conversationMapper(List<Conversation> externalConversations) {
        Map<String, ytuce.gp.mfmsp.Entity.Conversation> conversationMap = new HashMap<>();
        List<ytuce.gp.mfmsp.Entity.Conversation> conversations = conversationRepository.getConversationsByPlatform(Platform.FACEBOOK);

        for (ytuce.gp.mfmsp.Entity.Conversation conversation : conversations) {
            conversationMap.put(conversation.getExternalId(), conversation);
        }
        for (Conversation externalConversation : externalConversations) {
            if (externalConversation == null ||
                    externalConversation.getMessages() == null ||
                    externalConversation.getMessages().getData() == null) {
                continue;
            }
            String mapId = externalConversation.getId();
            int i = 0;
            while (i < externalConversation.getMessages().getData().size() && externalConversation.getMessages().getData().get(i).getFrom().getId().equals(pageId)) {
                i++;
            }
            if (i < externalConversation.getMessages().getData().size()) {
                mapId = externalConversation.getMessages().getData().get(i).getFrom().getId();
            }

            ytuce.gp.mfmsp.Entity.Conversation conversation = conversationMap.get(mapId);
            if (conversation == null) {
                conversation = new ytuce.gp.mfmsp.Entity.Conversation();
                conversation.setHasEnded(false);
                conversation.setPlatform(Platform.FACEBOOK);
                conversation.setExternalId(mapId);
            }
            addMessagesToConversation(externalConversation.getMessages().getData(), conversation);
            conversationMap.put(mapId, conversation);

        }
        for (ytuce.gp.mfmsp.Entity.Conversation conversation : conversationMap.values()) {
            List<ytuce.gp.mfmsp.Entity.Message> messageList = conversation.getMessages();
            messageList.sort(Comparator.comparing(ytuce.gp.mfmsp.Entity.Message::getTime));
        }

        return conversationRepository.saveAll(conversationMap.values());
    }

    private void addMessagesToConversation(List<Message> externalMessages, ytuce.gp.mfmsp.Entity.Conversation conversation) {
        for (Message externalMessage : externalMessages) {
            ytuce.gp.mfmsp.Entity.Message message = new ytuce.gp.mfmsp.Entity.Message();
            ZonedDateTime dateTime = ZonedDateTime.parse(externalMessage.getCreated_time(), formatter);
            message.setTime(dateTime.toInstant().toEpochMilli());
            message.setText(externalMessage.getMessage());
            message.setExternalId(externalMessage.getId());
            message.setDirection(externalMessage.getFrom().getId().equals(pageId));

            conversation.addMessage(message);
        }
    }


    private class Conversation {
        String id;
        Messages messages;

        public Conversation() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Messages getMessages() {
            return messages;
        }

        public void setMessages(Messages messages) {
            this.messages = messages;
        }

        @Override
        public String toString() {
            return "\nConversation{" +
                    "id='" + id + '\'' +
                    ", messages=" + messages +
                    '}';
        }
    }

    private class Messages {
        List<FacebookServiceImpl.Message> data;

        public Messages() {
        }

        public List<FacebookServiceImpl.Message> getData() {
            return data;
        }

        public void setData(List<FacebookServiceImpl.Message> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "\nMessages{" +
                    "data=" + data +
                    '}';
        }
    }

    private class Message {
        String id;
        String message;
        User from;
        Recipients to;
        String created_time;

        public Message() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public User getFrom() {
            return from;
        }

        public void setFrom(User from) {
            this.from = from;
        }

        public Recipients getTo() {
            return to;
        }

        public void setTo(Recipients to) {
            this.to = to;
        }

        public String getCreated_time() {
            return created_time;
        }

        public void setCreated_time(String created_time) {
            this.created_time = created_time;
        }

        @Override
        public String toString() {
            return "\nMessage{" +
                    "id='" + id + '\'' +
                    ", message='" + message + '\'' +
                    ", from=" + from +
                    ", to=" + to +
                    ", created_time='" + created_time + '\'' +
                    '}';
        }
    }

    private class User {
        String name;
        String email;
        String id;

        public User() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "\nUser{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    private class Recipients {
        List<User> data;

        public Recipients() {
        }

        public List<User> getData() {
            return data;
        }

        public void setData(List<User> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "\nRecipients{" +
                    "data=" + data +
                    '}';
        }
    }

    private class Root {
        List<FacebookServiceImpl.Conversation> data;

        public Root() {
        }

        public List<FacebookServiceImpl.Conversation> getData() {
            return data;
        }

        public void setData(List<FacebookServiceImpl.Conversation> data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "\nRoot{" +
                    "data=" + data +
                    '}';
        }
    }
}

