package txu.report.mainapp.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.dto.TimelineItemDto;
import txu.report.mainapp.dto.request.TaskTimelineRequest;
import txu.report.mainapp.dto.request.TaskTimelineStatusChangeRequest;
import txu.report.mainapp.dto.request.TaskTimelineCommentRequest;
import txu.report.mainapp.payload.CommentPayload;
import txu.report.mainapp.payload.StatusChangedPayload;
import txu.report.mainapp.service.TaskTimelineService;
import txu.report.mainapp.type.TimelineEventType;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class TaskTimelineApi {

    private final TaskTimelineService taskTimelineService;

    @PostMapping("/task-timeline")
    public List<TimelineItemDto> getTaskTimeline(@RequestBody TaskTimelineRequest request) {
        return taskTimelineService.getTaskTimeline(request.getTaskId());
    }

    @PostMapping("/task-timeline/comment")
    public void taskTimelineComment(@RequestBody TaskTimelineCommentRequest request) {
        CommentPayload payload = new CommentPayload();
        payload.setComment(request.getComment());

        taskTimelineService.saveEvent(
                request.getTaskId(),
                TimelineEventType.COMMENT,
                request.getActorId(),
                payload
        );
    }

    @PostMapping("/task-timeline/status-change")
    public void taskTimelineStatusChange(@RequestBody TaskTimelineStatusChangeRequest request) {
        StatusChangedPayload payload = new StatusChangedPayload();
        payload.setFromStatus(request.getFromStatus());
        payload.setToStatus(request.getToStatus());

        taskTimelineService.saveEvent(
                request.getTaskId(),
                TimelineEventType.STATUS_CHANGED,
                request.getActorId(),
                payload
        );
    }
}
