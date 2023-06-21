package ytuce.gp.mfmsp.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import net.minidev.json.annotate.JsonIgnore;
import org.glassfish.grizzly.http.server.util.StringParser;

import java.util.Objects;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String externalId;
    private String text;
    private Long time;
    private Boolean direction;
    private Boolean readStatus;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    public Long calculateWorkLoad() {
        String[] words = text.split(" ");
        if (direction) {
            return (long) words.length * 2L;
        }
        return (long) words.length;
    }

    public Message() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getDirection() {
        return direction;
    }

    public void setDirection(Boolean direction) {
        this.direction = direction;
    }

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        if (this.conversation != null) {
            this.conversation.getMessages().remove(this);
        }
        this.conversation = conversation;
        if (conversation != null) {
            conversation.getMessages().add(this);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", externalId='" + externalId + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", direction=" + direction +
                ", readStatus=" + readStatus +
                ", conversation=" + (conversation != null ? conversation.getId().toString() : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(externalId, message.externalId) && Objects.equals(text, message.text) && Objects.equals(time, message.time) && Objects.equals(direction, message.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalId, text, time, direction);
    }
}