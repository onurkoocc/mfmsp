package ytuce.gp.mfmsp.Pojo;

import lombok.Data;
import ytuce.gp.mfmsp.Entity.Message;

@Data
public class MessagePojo {
    private Integer id;
    private String externalId;
    private String text;
    private Long time;
    private Boolean direction;
    private Boolean readStatus;

    public static MessagePojo entityToPojoBuilder(Message message){
        if(message==null){
            return null;
        }
        MessagePojo messagePojo = new MessagePojo();
        messagePojo.setId(message.getId());
        messagePojo.setText(message.getText());
        messagePojo.setTime(message.getTime());
        messagePojo.setDirection(message.getDirection());
        messagePojo.setExternalId(message.getExternalId());
        messagePojo.setReadStatus(message.getReadStatus());
        return messagePojo;
    }
}
