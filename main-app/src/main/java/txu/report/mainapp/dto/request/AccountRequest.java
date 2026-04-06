package txu.report.mainapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountRequest {
    private long keyOffset;
    private int limit;
    private String keySearch;
}
