package txu.report.mainapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import txu.report.mainapp.type.TimelineEventType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "TASK_TIMELINE")
@Getter
@Setter
public class TaskTimelineEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TASK_ID")
    private Long taskId;

    @Column(name = "EVENT_TYPE")
    @Enumerated(EnumType.STRING)
    private TimelineEventType eventType;

    @Column(name = "ACTOR_USER_ID")
    private Long actorUserId;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "PAYLOAD_JSON")
    private String payloadJson;
}