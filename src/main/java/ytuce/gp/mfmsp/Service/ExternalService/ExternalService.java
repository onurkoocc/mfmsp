package ytuce.gp.mfmsp.Service.ExternalService;

public interface ExternalService {
    void sendMessage(String recipient, String messageText);
    void receiveMessages();
}
