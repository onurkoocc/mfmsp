package ytuce.gp.mfmsp.Service.ExternalService.Impl;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ytuce.gp.mfmsp.Constants.Platform;
import ytuce.gp.mfmsp.Repository.ConversationRepository;
import ytuce.gp.mfmsp.Repository.MessageRepository;
import ytuce.gp.mfmsp.Service.ExternalService.ExternalService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private MessageRepository messageRepository;

    private static final String accessToken = "EAAwXkaAKP3oBAMlnmP8hIWBI2mhZC3EBY9px7vPVcvV0FulsKYyqeV6pEraSLThytfrAncGVEHfHQ60exSdloZBelkRAjLbxqjlq33rKs8ileUrjKoFc56Qh7NHNSDvehFr3qjyNv5xEdXP7eZATDRbsyLiRAqJqAzL1I1fztZCwUBALZBmUAK93T05ZAMVJCy9TFVZCUGoZBUbZCcHbQwQ973kbTc1JZBBLEZD";

    private static final String pageId = "103158119328523";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
    /*
    Facebook send message
    curl -i -X POST \
 "https://graph.facebook.com/v15.0/103158119328523/messages?recipient=%7B%0A%20%20%22id%22%3A%20%225688324121263882%22%0A%7D&messaging_type=RESPONSE&message=%7B%0A%20%20%22text%22%3A%20%22Hello%20HELLO%2C%20world%20WORLD!%22%0A%7D&access_token=EAAwXkaAKP3oBAPhmGvtLyQallFWdUVhXLy8D5YPANunAFkQBj9v9D4eeEdquoXBIV0TT2m5zrmKsUBWrrX4JTzWfx2RXEJdE4AyiTBte8Aqk9fACLyKvJfKHu8Uxk1wb3YvyGfUXs1mbDyjaLfrSTi4VYEknd00IuMS9XYVUSR2UyTW1Dll8ZBrE6DBi2fZAvUByaLsqqnJgZBYTlvpiMSZCQfjJeL4ZD"
     */

    public void sendMessage(String recipientId, String messageText) {
        try {
            // Create the JSON objects for the payload
            JSONObject recipient = new JSONObject();
            recipient.put("id", recipientId);

            JSONObject message = new JSONObject();
            message.put("text", messageText);

            // Create the URL for the request
            String url = "https://graph.facebook.com/v15.0/103158119328523/messages?" +
                    "recipient=" + recipient.toString() +
                    "&messaging_type=RESPONSE" +
                    "&message=" + message.toString() +
                    "&access_token=" + accessToken;

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            // Set the request method (POST), content type, and enable input/output
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send the POST request
            OutputStream os = conn.getOutputStream();
            os.write(url.getBytes(StandardCharsets.UTF_8));
            os.close();

            // Print the response code/message for debugging
            System.out.println("Response Code : " + conn.getResponseCode());
            System.out.println("Response Message : " + conn.getResponseMessage());

            // If the request was not successful, print the error message
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

    /*
    //Facebook get messages
    curl -i -X GET \
 "https://graph.facebook.com/v15.0/103158119328523/conversations?fields=messages%7Bmessage%2Cid%2Cto%2Cfrom%2Ccreated_time%7D&platform=MESSENGER&access_token=EAAwXkaAKP3oBAOkRhd3BOrc3opXxruhonB2AHmwZAeCvmRZCGkJd56QjiMLgDcpaBi5PHRuhkrHHsp2x8w1mZCVoZBVUPlEZBabY9G0NIsytb9TTt5eBmk2aIqDMZBiEEVNBCcNUiqs07gGX3a1ymL5zkm4O4dV7XU5AvRmXNTaCpayZAj35jeFOW8nn8dzqQVbkZBZADySpyQGHQwtmJN6e2z1jRUwWdUhcZD"
     */
    //@Autowired

    public void receiveMessages() {
        try {
            // Create the URL for the request
            String url = "https://graph.facebook.com/v15.0/103158119328523/conversations?" +
                    "fields=messages%7Bmessage%2Cid%2Cto%2Cfrom%2Ccreated_time%7D" +
                    "&platform=MESSENGER" +
                    "&access_token=" + accessToken;

            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            // Set the request method (GET)
            conn.setRequestMethod("GET");

            // Print the response code/message for debugging
            System.out.println("Response Code : " + conn.getResponseCode());
            System.out.println("Response Message : " + conn.getResponseMessage());

            // If the request was not successful, print the error message
            if (conn.getResponseCode() != 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                String output;
                System.out.println("Error from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            } else { // If the request was successful, print the response
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
            ytuce.gp.mfmsp.Entity.Conversation conversation = conversationMap.get(mapId);
            if (conversation == null) {
                conversation = new ytuce.gp.mfmsp.Entity.Conversation();
                conversation.setPlatform(Platform.FACEBOOK);
                conversation.setExternalId(mapId);
            }
            addMessagesToConversation(externalConversation.getMessages().getData(), conversation);
            conversationMap.put(mapId, conversation);

        }
        for (ytuce.gp.mfmsp.Entity.Conversation conversation : conversationMap.values()) {
            List<ytuce.gp.mfmsp.Entity.Message> messageList = conversation.getMessages();
            messageList.sort(Comparator.comparing(ytuce.gp.mfmsp.Entity.Message::getTime));
            // The replace operation is not needed here as we're manipulating the same conversation object.
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


    /*
    private void conversationMapper(Root root){
        if(root == null || root.getData() == null || root.getData().isEmpty()){
            return;
        }

        for(Conversation externalConversation : root.getData()){
            if(externalConversation == null){
                continue;
            }
            handleConversation(externalConversation);
        }
    }

    private void handleConversation(Conversation externalConversation) {
        ytuce.gp.mfmsp.Entity.Conversation conversation = null;

        if( conversationRepository.existsByExternalId(externalConversation.getId()) ) {
            conversation = conversationRepository.getConversationByExternalId(externalConversation.getId());
        }

        if(conversation == null) {
            conversation = new ytuce.gp.mfmsp.Entity.Conversation();
            conversation.setPlatform(Platform.FACEBOOK);
        }

        setConversationMessages(externalConversation, conversation);

        if(!conversationRepository.existsByExternalId(externalConversation.getId())) {
            conversationRepository.save(conversation);
        }
    }

    private void setConversationMessages(Conversation externalConversation, ytuce.gp.mfmsp.Entity.Conversation conversation) {
        if (externalConversation.getMessages() == null || externalConversation.getMessages().getData() == null) {
            conversation.setMessages(null); // Is it better to set null or empty array?
            return;
        }

        if (conversation.getMessages() == null) {
            conversation.setMessages(createMessages(externalConversation.getMessages().getData()));
            return;
        }
        if(externalConversation.getMessages().getData().size() != conversation.getMessages().size()){
            conversation.setMessages(copyExternalMessagesToMessages(externalConversation.getMessages().getData(),conversation.getMessages()));
        }
    }

    private List<ytuce.gp.mfmsp.Entity.Message> copyExternalMessagesToMessages(List<FacebookServiceImpl.Message> externalMessages, List<ytuce.gp.mfmsp.Entity.Message> messages){
        if(externalMessages.size()>messages.size()){
            for(int i=messages.size();i<externalMessages.size();i++){
                messages.add(setMessageFromExternalMessage(externalMessages.get(i)));
            }
        }else if(externalMessages.size()<messages.size()){
            for(int i=messages.size()-1;i>=externalMessages.size();i--){
                ytuce.gp.mfmsp.Entity.Message message = messages.get(i);
                messages.remove(i);
                messageRepository.delete(message);
            }
        }
        return messages;
    }

    private List<ytuce.gp.mfmsp.Entity.Message> createMessages(List<FacebookServiceImpl.Message> externalMessages){
        List<ytuce.gp.mfmsp.Entity.Message> messages = new ArrayList<>();
        for(FacebookServiceImpl.Message externalMessage : externalMessages){
            if(externalMessage == null){
                continue;
            }
            messages.add(setMessageFromExternalMessage(externalMessage));
        }

        return messages;
    }

    private ytuce.gp.mfmsp.Entity.Message setMessageFromExternalMessage(FacebookServiceImpl.Message externalMessage){
        ytuce.gp.mfmsp.Entity.Message message = new ytuce.gp.mfmsp.Entity.Message();
        message.setExternalId(externalMessage.getId());
        message.setText(externalMessage.getMessage());

        if(externalMessage.getFrom()!=null && externalMessage.getFrom().getId()!=null){
            message.setDirection(externalMessage.getFrom().getId().equals(pageId));
        }else{
            message.setDirection(null); // Review if this part is suitable
        }

        ZonedDateTime dateTime = ZonedDateTime.parse(externalMessage.getCreated_time(), formatter);
        message.setTime(dateTime.toInstant().toEpochMilli());
        return message;
    }

*/

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

    /*
    private List<ytuce.gp.mfmsp.Entity.Conversation> conversationMapper(Root root){
        if(root !=null && root.getData()!=null && !root.getData().isEmpty()){
            for(Conversation externalConversation :root.getData()){
                if(externalConversation!=null){
                    if( conversationRepository.existsByExternalId(externalConversation.getId()) ){
                        ytuce.gp.mfmsp.Entity.Conversation conversation = conversationRepository.getConversationByExternalId(externalConversation.getId());
                        boolean isSame = true;

                        if (conversation != null) {
                            if (conversation.getMessages() != null) {
                                if (externalConversation.getMessages() != null && externalConversation.getMessages().getData() != null) {
                                    if (externalConversation.getMessages().getData().size() != conversation.getMessages().size()) {
                                        conversation.setMessages(copyExternalMessagesToMessages(externalConversation.getMessages().getData()));
                                    }
                                } else {
                                    //Null mı boş array mi atmak mantıklı?
                                    conversation.setMessages(null);
                                }
                            } else {
                                if (externalConversation.getMessages() != null && externalConversation.getMessages().getData() != null) {
                                    conversation.setMessages(copyExternalMessagesToMessages(externalConversation.getMessages().getData()));
                                }
                            }
                        } else {
                            conversation = new ytuce.gp.mfmsp.Entity.Conversation();
                            if (externalConversation.getMessages() != null && externalConversation.getMessages().getData() != null) {
                                conversation.setMessages(copyExternalMessagesToMessages(externalConversation.getMessages().getData()));
                            } else {
                                //Null mı boş array mi atmak mantıklı?
                                conversation.setMessages(null);
                            }
                        }
                    }else{

                        ytuce.gp.mfmsp.Entity.Conversation conversation = new ytuce.gp.mfmsp.Entity.Conversation();
                        if (externalConversation.getMessages() != null && externalConversation.getMessages().getData() != null) {
                            conversation.setMessages(copyExternalMessagesToMessages(externalConversation.getMessages().getData()));
                        } else {
                            //Null mı boş array mi atmak mantıklı?
                            conversation.setMessages(null);
                        }
                        conversationRepository.save(conversation);
                    }
                }
            }
        }
        return null;
    }

    private List<ytuce.gp.mfmsp.Entity.Message> copyExternalMessagesToMessages(List<FacebookServiceImpl.Message> externalMessages){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        List<ytuce.gp.mfmsp.Entity.Message> messages = new ArrayList<>();
        for(FacebookServiceImpl.Message externalMessage : externalMessages){
            if(externalMessage!=null){
                ytuce.gp.mfmsp.Entity.Message message = new ytuce.gp.mfmsp.Entity.Message();
                message.setExternalId(externalMessage.getId());
                message.setText(externalMessage.getMessage());
                if(externalMessage.getFrom()!=null && externalMessage.getFrom().getId()!=null){
                    message.setDirection(externalMessage.getFrom().getId().equals(pageId));
                }else{
                    //Bu kısım uygun mu gözden geçir
                    message.setDirection(false);
                }
                ZonedDateTime dateTime = ZonedDateTime.parse(externalMessage.getCreated_time(), formatter);
                message.setTime(dateTime.toInstant().toEpochMilli());
                messages.add(message);
            }
        }
        return messages;
    }

 */
    /*
   public static void fbcodes(){
       try {
           String recipientId =   "5688324121263882";
           String messageText = " Pazar gecesi : "+System.nanoTime(); // Replace with message text
           // make HTTP request
           String pageAccessToken = "EAAwXkaAKP3oBAHiDAU8JMc7ZAuJOGpgvjMVaiPlomNKOa3WpKqGyyAdVZBnRUtjUwNDpLOl73ScHq8G1UJcCRLOBpfEY0hYp8E5iLcONaslz0cS640Cwvv59lSzXwvaowLBNJcsBqZADFT1yxjgKAek0PiKGHkH3HkObZALGsyS0ESUyJkIiu8Db34gsClkDmDfPHF2Vp0ZA9oB4F2tKV5UHcjRC7ABEZD";
           String pageId = "103158119328523";
           String apiUrl = "https://graph.facebook.com/v15.0/" + pageId + "/messages";

           String queryString = "recipient=" + URLEncoder.encode("{\"id\":\"" + recipientId + "\"}", StandardCharsets.UTF_8) +
                   "&messaging_type=RESPONSE" +
                   "&message=" + URLEncoder.encode("{\"text\":\"" + messageText + "\"}", StandardCharsets.UTF_8) +
                   "&access_token=" + URLEncoder.encode(pageAccessToken, StandardCharsets.UTF_8);

           URL url = new URL(apiUrl + "?" + queryString);
           HttpURLConnection con = (HttpURLConnection) url.openConnection();
           con.setRequestMethod("POST");

           int status = con.getResponseCode();

           BufferedReader in = new BufferedReader(
                   new InputStreamReader(con.getInputStream()));
           String inputLine;
           StringBuilder content = new StringBuilder();
           while ((inputLine = in.readLine()) != null) {
               content.append(inputLine);
           }
           in.close();

           System.out.println("Response status: " + status);
           System.out.println("Response content: " + content.toString());

           ObjectMapper mapper = new ObjectMapper();
           Root root = mapper.readValue(content.toString(), Root.class);

           // Now you can use the data in an object-oriented way
           for (Conversation conversation : root.getData()) {
               for (Message message : conversation.getMessages().getData()) {
                   User fromUser = message.getFrom();
                   Recipients toUsers = message.getTo();
                   // And so on...
               }
           }
       } catch (IOException e) {
           System.out.println("Error: " + e.getMessage());
           e.printStackTrace();
       }
   }

     */
}

