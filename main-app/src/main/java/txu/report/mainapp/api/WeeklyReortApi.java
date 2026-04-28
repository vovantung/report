package txu.report.mainapp.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.*;
import txu.report.mainapp.entity.WeeklyReportEntity;
import txu.report.mainapp.service.WeeklyReportService;
import txu.report.mainapp.util.JwtUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class WeeklyReortApi extends AbstractApi {

    private final WeeklyReportService weeklyReportService;

    @PostMapping(value = "/admin/weekly-reports/date-range")
    public List<WeeklyReportDto> findReportsByDateRange(@RequestBody FromDateToDateRequest request){
        return weeklyReportService. findReportsByDateRange(request.getFrom(), request.getTo());
    }
    @PostMapping(value = "/admin/weekly-reports/departments/without-reports")
    public List<DepartmentDto> findDepartmentsWithoutReportsInDateRange(@RequestBody FromDateToDateRequest request){
        return weeklyReportService.findDepartmentsWithoutReportsInDateRange(request.getFrom(), request.getTo());
    }

    @DeleteMapping(value = "/admin/weekly-reports/remove")
    public boolean removeById(@RequestBody IdRequest request){
        return weeklyReportService.removeById(request.getId());
    }

    //User
    @PostMapping("/user/weekly-reports/get-presignedurl-for-get")
    public LinkDto getPreSignedUrlForGeUser(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            String pre_signed_url =  weeklyReportService.getPreSignedUrlForGet(request.getFilename());
            linkDto.setPre_signed_url(pre_signed_url);
        } catch (Exception e) {

        }
        return linkDto;
    }

    @PostMapping("/user/weekly-reports/get-presignedurl-for-put")
    public LinkDto getPreSignedUrlForPuUser(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            return weeklyReportService.getPreSignedUrlForPut(request.getFilename());
        } catch (Exception e) {

        }
        return linkDto;
    }

    @PostMapping("/user/weekly-reports/add")
    public ResponseEntity<?> addReportUser(@RequestBody UploadfileInfoRequest request, HttpServletRequest httpServletRequest) throws Exception {

        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String,Object> claims = JwtUtils.decode(token);

        try {
            WeeklyReportEntity weeklyReport = weeklyReportService.addReport(request, claims.get("preferred_username").toString());
            return ResponseEntity.ok(weeklyReport);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping(value = "/user/weekly-reports/current-department/reports")
    public List<WeeklyReportExtends> getCurrentDepartmentReportsByDateRange(@RequestBody FromDateToDateRequest request, HttpServletRequest httpServletRequest) throws Exception {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String,Object> claims = JwtUtils.decode(token);
        return weeklyReportService. getCurrentDepartmentReportsByDateRange(request.getFrom(), request.getTo(), claims.get("preferred_username").toString());
    }

    @PostMapping(value = "/user/weekly-reports/summary/reports")
    public List<WeeklyReportExtends> getSummaryReportsByDateRange(@RequestBody FromDateToDateRequest request, HttpServletRequest httpServletRequest) throws Exception {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String,Object> claims = JwtUtils.decode(token);
        return weeklyReportService. getSummaryReportsByDateRange(request.getFrom(), request.getTo(), claims.get("preferred_username").toString());
    }
}
