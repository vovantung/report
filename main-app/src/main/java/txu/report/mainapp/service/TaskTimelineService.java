package txu.report.mainapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import txu.report.mainapp.dao.TaskTimelineDao;
import txu.report.mainapp.dto.TimelineItemDto;
import txu.report.mainapp.entity.TaskTimelineEntity;
import txu.report.mainapp.payload.CommentPayload;
import txu.report.mainapp.payload.StatusChangedPayload;
import txu.report.mainapp.type.TimelineEventType;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskTimelineService {

    private final TaskTimelineDao taskTimelineDao;
    private final ObjectMapper objectMapper;

    public List<TimelineItemDto> getTaskTimeline(Long taskId) {
        return taskTimelineDao.getBytaskId(taskId).stream().map(this::mapToDto).toList();
    }

    private TimelineItemDto mapToDto(TaskTimelineEntity entity) {
        return switch (entity.getEventType()) {

            case COMMENT -> mapComment(entity);

            case STATUS_CHANGED -> mapStatusChanged(entity);

//            case ASSIGNEE_CHANGED -> mapAssigneeChanged(entity);
//
//            case STEP_MOVED -> mapStepMoved(entity);

            default -> throw new RuntimeException("Unsupported type");
        };
    }

    private TimelineItemDto mapComment(TaskTimelineEntity entity) {
        try {
            CommentPayload payload = objectMapper.readValue(
                    entity.getPayloadJson(),
                    CommentPayload.class
            );

            return TimelineItemDto.builder()
                    .id(entity.getId())
                    .actorId(entity.getActorUserId())
                    .type("COMMENT")
                    .title("User commented")
                    .content(payload.getComment())
                    .payload(payload)
                    .createdAt(entity.getCreatedAt())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TimelineItemDto mapStatusChanged(TaskTimelineEntity entity) {
        try {
            StatusChangedPayload payload = objectMapper.readValue(
                    entity.getPayloadJson(),
                    StatusChangedPayload.class
            );

            return TimelineItemDto.builder()
                    .id(entity.getId())
                    .actorId(entity.getActorUserId())
                    .type("STATUS_CHANGED")
                    .title("Status changed")
                    .content(payload.getFromStatus()
                            + " → "
                            + payload.getToStatus())
                    .payload(payload)
                    .createdAt(entity.getCreatedAt())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void saveEvent(
            Long taskId,
            TimelineEventType eventType,
            Long actorUserId,
            Object payload
    ) {
        try {
            TaskTimelineEntity entity = new TaskTimelineEntity();
            entity.setTaskId(taskId);
            entity.setEventType(eventType);
            entity.setActorUserId(actorUserId);
            entity.setCreatedAt(LocalDateTime.now());
            entity.setPayloadJson(objectMapper.writeValueAsString(payload));

            taskTimelineDao.save(entity);

        } catch (Exception ex) {
            throw new RuntimeException("Cannot save timeline event", ex);
        }
    }
}