package socialnetwork.domain;

import socialnetwork.utils.DateConstants;

import java.time.LocalDateTime;

/**
 *  class that defines a message
 */
public class Message extends Entity<Long>{

    private User from;
    private Group to;
    private String message;
    private Message reply = null;
    private LocalDateTime date;

    /**
     *  constructor
     * @param id - id of the message
     * @param from - user who sent the message
     * @param message - the message
     * @param date - date the message was sent
     */
    public Message(Long id, User from, String message, LocalDateTime date) {
        super.setId(id);
        this.from = from;
        this.message = message;
        this.date = date;
    }

    /**
     *  constructor
     * @param id - id of the message
     * @param from - user who sent the message
     * @param to - group where the message was sent
     * @param message - the message
     * @param date - date when the message was sent
     */
    public Message(Long id, User from, Group to, String message, LocalDateTime date) {
        super.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
    }

    /**
     *  constructor
     * @param from - user who sent the message
     * @param to - group where the message was sent
     * @param message - the message
     * @param reply - replied message
     */
    public Message(User from, Group to, String message, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.reply = reply;
        this.date = LocalDateTime.now();
    }

    /**
     *  constructor
     * @param id - id of the message
     */
    public Message(Long id) {
        this.setId(id);
    }

    /**
     *  constructor
     * @param id - id of the message
     * @param message - the message
     */
    public Message(Long id, String message) {
        this.setId(id);
        this.message = message;
    }

    /**
     *  constructor
     * @param id - id of the message
     * @param from - user who sent the message
     * @param to - group where the message was sent
     * @param message - the message
     */
    public Message(Long id, User from, Group to, String message) {
        this.setId(id);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = LocalDateTime.now();
    }

    /**
     *  from getter
     * @return user who sent the message
     */
    public User getFrom() {
        return from;
    }

    /**
     *  to getter
     * @return group where the message was sent
     */
    public Group getTo() {
        return to;
    }

    /**
     *  message getter
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     *  reply getter
     * @return replied message
     */
    public Message getReply() {
        return reply;
    }

    /**
     *  date getter
     * @return date when the message was sent
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     *  reply setter
     * @param reply - new replied message
     */
    public void setReply(Message reply) {
        this.reply = reply;
    }

    /**
     *  truncates the messages of the reply
     * @param reply - replied to message
     * @return truncated message
     */
    private String getReplyShow(Message reply){
        String message = reply.getMessage();
        String[] rows = message.split("\\r?\\n");
        if (rows.length > 1)
            message = rows[0];
        int minim = Integer.min(20, message.length());
        message = message.substring(0, minim);
        if (minim == 20 || rows.length > 1)
            message += "...";
        return message;
    }

    @Override
    public String toString() {
        if (reply == null)
            return from.getFirstName() + " " + from.getLastName() + ": " + "\n" + message + "\nAt " + date.format(DateConstants.DATE_TIME_FORMATTER);
        return from.getFirstName() + " " + from.getLastName() + " replied to: " + getReplyShow(reply)  + "\n" + message + "\nAt " + date.format(DateConstants.DATE_TIME_FORMATTER);
    }
}
