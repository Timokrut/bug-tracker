package tracker.model;

import java.time.LocalDateTime; 

public class Ticket {
    public int id;
    public String title;
    public String description;
    public TicketType type;
    public TicketStatus status;
    public TicketPriority priority;
    
    public int authorId;
    public int assigneeId;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public TicketType getType() {
        return this.type;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public TicketStatus getStatus() {
        return this.status;
    }
    
    public Integer getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
    }


}
