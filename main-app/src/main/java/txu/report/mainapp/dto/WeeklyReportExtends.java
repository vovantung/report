package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;
import txu.report.mainapp.entity.WeeklyReportEntity;

import java.util.Date;

@Getter
@Setter
public class WeeklyReportExtends extends WeeklyReportEntity {
    String urlReportEx;
    String originNameReportEx;
    String filenameReportEx;
    Date dateReportEx;
}
