package txu.report.mainapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskTimelineCommentRequest {
    private Long taskId;
    private Long actorId;
    private String comment;
}
