package txu.report.mainapp.payload;

import lombok.Data;

@Data
public class StatusChangedPayload {
    private String fromStatus;
    private String toStatus;
}