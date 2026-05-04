package txu.report.mainapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTimelineStatusChangeRequest {
    private Long taskId;
    private Long actorId;
    private String fromStatus;
    private String toStatus;
}
