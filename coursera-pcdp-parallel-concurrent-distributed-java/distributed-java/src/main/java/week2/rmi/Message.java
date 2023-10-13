package week2.rmi;

import java.io.Serializable;
import java.util.Objects;

public class Message implements Serializable {

    private String messageText;

    private String contentType;

    public Message() {
    }

    public Message(String messageText, String contentType) {

        this.messageText = messageText;
        this.contentType = contentType;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var message = (Message) o;
        return Objects.equals(messageText, message.messageText) && Objects.equals(contentType, message.contentType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageText, contentType);
    }
}