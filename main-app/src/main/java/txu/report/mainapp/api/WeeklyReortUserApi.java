package txu.report.mainapp.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import txu.report.mainapp.base.AbstractApi;
import txu.report.mainapp.dto.*;
import txu.report.mainapp.entity.WeeklyReportEntity;
import txu.report.mainapp.service.WeeklyReportUserService;
import txu.report.mainapp.util.JwtUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report/user/weekly-report")
@RequiredArgsConstructor
public class WeeklyReortUserApi extends AbstractApi {

    private final WeeklyReportUserService weeklyReportService;

    @PostMapping("/get-presignedurl-for-get")
    public LinkDto getPreSignedUrlForGet(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            String pre_signed_url =  weeklyReportService.getPreSignedUrlForGet(request.getFilename());
            linkDto.setPre_signed_url(pre_signed_url);
        } catch (Exception e) {

        }
        return linkDto;
    }

    @PostMapping("/get-presignedurl-for-put")
    public LinkDto getPreSignedUrlForPut(@RequestBody LinkRequest request) {
        LinkDto linkDto = new LinkDto();
        try {
            return weeklyReportService.getPreSignedUrlForPut(request.getFilename());
        } catch (Exception e) {

        }
        return linkDto;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReport(@RequestBody UploadfileInfoRequest request, HttpServletRequest httpServletRequest) throws Exception {

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

    @PostMapping(value = "get-department-fromto")
    public List<WeeklyReportExtends> getDepartmentFromTo(@RequestBody FromDateToDateRequest request, HttpServletRequest httpServletRequest) throws Exception {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String,Object> claims = JwtUtils.decode(token);
        return weeklyReportService. getDepartmentFromTo(request.getFrom(), request.getTo(), claims.get("preferred_username").toString());
    }

    @PostMapping(value = "get-summary-fromto")
    public List<WeeklyReportExtends> getSummaryReportFromTo(@RequestBody FromDateToDateRequest request, HttpServletRequest httpServletRequest) throws Exception {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String token = authHeader.replace("Bearer ", "");
        Map<String,Object> claims = JwtUtils.decode(token);
        return weeklyReportService. getSummaryReportFromTo(request.getFrom(), request.getTo(), claims.get("preferred_username").toString());
    }

}
