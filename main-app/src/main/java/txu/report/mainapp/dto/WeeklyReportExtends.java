package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class WeeklyReportExtends extends WeeklyReportDto {
    String urlReportEx;
    String originNameReportEx;
    String filenameReportEx;
    Date dateReportEx;

    public WeeklyReportExtends(){
        super();
    }
}
