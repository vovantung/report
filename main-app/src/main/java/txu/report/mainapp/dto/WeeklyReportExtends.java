package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;
import txu.report.mainapp.entity.WeeklyReportEntity;

import java.util.Date;

@Getter
@Setter
public class WeeklyReportExtends extends WeeklyReportDto {
    String urlReportEx;
    String originNameReportEx;
    String filenameReportEx;
    Date dateReportEx;

    public WeeklyReportExtends(Long id, String filename, String originName, String url, Date uploadedAt, DepartmentDto department) {
        super(id, filename, originName, url, uploadedAt, department);
    }

    public WeeklyReportExtends(){
        super();
    }
}
