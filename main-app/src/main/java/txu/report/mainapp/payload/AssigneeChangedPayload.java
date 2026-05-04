package txu.report.mainapp.payload;

import lombok.Data;

@Data
public class AssigneeChangedPayload {
    private Long fromUserId;
    private String fromUserName;

    private Long toUserId;
    private String toUserName;
}