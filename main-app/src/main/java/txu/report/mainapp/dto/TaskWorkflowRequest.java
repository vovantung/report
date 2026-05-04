package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskWorkflowRequest {
    String title;
    String description;
    Long workflowId;
}
