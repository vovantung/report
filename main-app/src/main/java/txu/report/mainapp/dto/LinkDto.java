package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkDto {
    private String pre_signed_url;
    private String filename;
}
