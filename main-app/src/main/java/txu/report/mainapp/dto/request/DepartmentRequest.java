package txu.report.mainapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequest {
    private long keyOffset;
    private int limit;
    private String keySearch;
}
