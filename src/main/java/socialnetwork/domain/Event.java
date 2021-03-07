package socialnetwork.domain;

import socialnetwork.utils.DateConstants;

import java.time.LocalDateTime;

public class Event extends Entity<Long> {

    private final User user;
    private final String title, description;
    private LocalDateTime start_time, end_time;
    private boolean notify = true;

    public Event(Long id, String title, String description) {
        this.setId(id);
        this.user = new User();
        this.title = title;
        this.description = description;
    }

    public Event(Long id, User user, String title, String description, LocalDateTime start_time, LocalDateTime end_time) {
        this.setId(id);
        this.user = user;
        this.title = title;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public Event(Long id, User user, String title, String description, LocalDateTime start_time, LocalDateTime end_time, boolean notify) {
        this.setId(id);
        this.user = user;
        this.title = title;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
        this.notify = notify;
    }

    public Event(User user, String title, String description, LocalDateTime start_time, LocalDateTime end_time) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public User getUser() {
        return user;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public String getStart_timeString() {
        return start_time.format(DateConstants.DATE_TIME_FORMATTER);
    }

    public String getEnd_timeString() {
        return end_time.format(DateConstants.DATE_TIME_FORMATTER);
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }
}
