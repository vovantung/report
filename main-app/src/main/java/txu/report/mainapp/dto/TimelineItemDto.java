package txu.report.mainapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TimelineItemDto {

    private Long id;
    private String type;
    private Long actorId;
    private LocalDateTime createdAt;

    private String title;
    private String content;

    private Object payload;
}
