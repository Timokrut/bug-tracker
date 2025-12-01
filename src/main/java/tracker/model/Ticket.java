package tracker.model;

import java.time.LocalDateTime; 

public class Ticket {
    public int id;
    public String title;
    public String description;
    public TicketType type;
    public TicketStatus status;
    public TicketPriority priority;
    public int assigneeId;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}
