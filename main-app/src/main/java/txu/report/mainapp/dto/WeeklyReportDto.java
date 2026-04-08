package txu.report.mainapp.dto;

import lombok.Getter;
import lombok.Setter;
import txu.report.mainapp.entity.DepartmentEntity;

import java.util.Date;

@Getter
@Setter
public class WeeklyReportDto {
    private Long id;
    private String filename;
    private String originName;
    private String url;
    private Date uploadedAt;
    private DepartmentDto department;
    public WeeklyReportDto(Long id, String filename, String originName, String url, Date uploadedAt, DepartmentDto department) {
        this.id = id;
        this.filename = filename;
        this.originName = originName;
        this.url = url;
        this.uploadedAt = uploadedAt;
        this.department = department;
    }
}
