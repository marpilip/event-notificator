package event.notificator.model;

import java.time.LocalDateTime;
import java.util.Map;

public class Notification {
    private Long id;
    private Long eventId;
    private Map<String, Object> changedFields;
    private String message;
    private LocalDateTime createdAt;
    private Boolean isRead;

    public Notification() {
    }

    public Notification(Long id, Long eventId, Map<String, Object> changedFields,
                        String message, LocalDateTime createdAt, Boolean isRead) {
        this.id = id;
        this.eventId = eventId;
        this.changedFields = changedFields;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Map<String, Object> getChangedFields() {
        return changedFields;
    }

    public void setChangedFields(Map<String, Object> changedFields) {
        this.changedFields = changedFields;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean read) {
        isRead = read;
    }
}