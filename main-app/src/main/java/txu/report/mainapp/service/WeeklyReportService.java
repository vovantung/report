package txu.report.mainapp.service;

import com.amazonaws.AmazonServiceException;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import txu.report.mainapp.dao.AccountDao;
import txu.report.mainapp.dao.DepartmentDao;
import txu.report.mainapp.dao.WeeklyReportDao;
import txu.report.mainapp.dto.*;

import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.DepartmentEntity;
import txu.report.mainapp.entity.WeeklyReportEntity;
import txu.common.exception.NotFoundException;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static txu.report.mainapp.common.DateUtil.*;

@Service
@RequiredArgsConstructor
public class WeeklyReportService {

    private final WeeklyReportDao weeklyReportDao;
    private final DepartmentDao departmentDao;
    private final AccountService accountService;
    private final AccountDao accountDao;

    private final S3Client s3Client;

    @Value("${ceph.rgw.bucket}")
    private String bucketName;

    @Value("${ceph.rgw.endpoint}")
    private String url;

    private final S3Presigner presigner;

    public LinkDto getPreSignedUrlForPut(String key) {
        LinkDto linkDto = new LinkDto();
        String filename = UUID.randomUUID() + "_" + key;
        PutObjectRequest objectRequest = PutObjectRequest.builder().bucket(bucketName).key(filename).build();
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(2)).putObjectRequest(objectRequest).build();
        String pre_signed_url = presigner.presignPutObject(presignRequest).url().toString();
        linkDto.setPre_signed_url(pre_signed_url);
        linkDto.setFilename(filename);
        return linkDto;
    }

    public String getPreSignedUrlForGet(String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder().signatureDuration(Duration.ofMinutes(15)).getObjectRequest(getRequest).build();
        String pre_signed_url = presigner.presignGetObject(presignRequest).url().toString();
        return pre_signed_url;
    }

    public WeeklyReportEntity addReport(UploadfileInfoRequest request, String username) throws Exception {
        AccountEntity account = accountDao.getByUsername(username);
        // Nếu tồn tại những thông tin report trong tuần mà liên qua đến người dùng (thuộc phòng ban) đã upload report hiện tại thì
        // xóa hết report đã upload trên lên storage1 (ngoại trừ file báo cáo hiện tại), và xóa tất cả dữ liệu lưu ở cơ sở dữ liệu (trong tuần hiện tại)
        List<WeeklyReportDto> weeklyReportEntities = findReportsByDateRange(toDate(getStartOfWeek()), toDate(getEndOfWeek().plusDays(1).atStartOfDay().toLocalDate()));
        weeklyReportEntities.forEach(weeklyReportEntity -> {
//            if (weeklyReportEntity.getDepartment().getId() == userDetails.getDepartment_id()) {
            if (Objects.equals(weeklyReportEntity.getDepartment().getId(), account.getDepartment().getId())) {
                if (!Objects.equals(weeklyReportEntity.getFilename(), request.getFilename())) {
                    try {
                        s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(weeklyReportEntity.getFilename()).build()
                        );
                        System.out.println("Deleted successfully: " + weeklyReportEntity.getFilename());
                    } catch (AmazonServiceException e) {
                        System.out.println("AWS Service error when deleting object. " + e);
                    } catch (SdkClientException e) {
                        System.out.println("AWS SDK client error when deleting object " + e);
                    }
                }
                // Xóa dữ liệu
                weeklyReportDao.delete(weeklyReportDao.getById(weeklyReportEntity.getId()));
            }
        });
        // Thêm kiểm tra file báo cáo có tồn tại trên bucket chưa, nếu chưa thì không cập nhật dữ liệu
        String fileUrl = String.format(url + "/%s/%s", bucketName, request.getFilename());
        // Save metadata
        DepartmentEntity department = null;
        if (account != null) {
            department = departmentDao.getById(account.getDepartment().getId());
        }

        WeeklyReportEntity weeklyReport = new WeeklyReportEntity();
        weeklyReport.setFilename(request.getFilename());
        weeklyReport.setUrl(fileUrl);
        weeklyReport.setOriginName(request.getFilenameOrigin());
        weeklyReport.setDepartment(department);
        weeklyReport.setUploadedAt(DateTime.now().toDate());
        return weeklyReportDao.save(weeklyReport);
    }

    public List<WeeklyReportDto> findReportsByDateRange(Date from, Date to) {
        List<Object[]> rows = weeklyReportDao.findReportsByDateRange(from, to);
        Map<Long, WeeklyReportDto> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Long weeklyReportId = ((Number) row[0]).longValue();
            String filename = (String) row[1];
            String originName = (String) row[2];
            String url = (String) row[3];
            Date uploadedAt = (Date) row[4];
            Integer departmentId = ((Number)row[5]).intValue();
            String departmentName = (String) row[6];
            DepartmentDto departmentDto = new DepartmentDto();
            departmentDto.setId(departmentId);
            departmentDto.setName(departmentName);
            // tạo department nếu chưa có
            map.computeIfAbsent(weeklyReportId, id -> new WeeklyReportDto(id, filename, originName, url,uploadedAt, departmentDto));
        }
        List<WeeklyReportDto> rs = new ArrayList<>(map.values());
        return rs;
    }

    public List<DepartmentDto> findDepartmentsWithoutReport_(Date from, Date to) {
        // Cách này tối ưu hơn khi dùng HashSet để lưu các departmentId thay vì ArrayList phục vụ cho việc
        // tìm kiếm, tuy nhiên vân phải lấy danh sách các departments và các weekly-report từ DB
        // Cách tối ưu nhật là tìm và lấy departments chưa báo cáo ngay ở DB với Sub Query (ANTI JOIN) như cách bên dưới
        List<DepartmentDto> departmentNoReport = new ArrayList<>();
        Set<Integer> departmentIds = findReportsByDateRange(from, to).stream()
                .map(report -> report.getDepartment().getId())
                .collect(Collectors.toSet());

        List<Department1Dto> department1Dtos = departmentDao.getPaging(1,100,"");
        department1Dtos.forEach(department -> {
            if (!departmentIds.contains(department.getId())) {
                DepartmentDto dpm = new DepartmentDto();
                dpm.setId(department.getId());
                dpm.setName(department.getName());
                departmentNoReport.add(dpm);
            }
        });
        return departmentNoReport;
    }

    public List<DepartmentDto> findDepartmentsWithoutReportsInDateRange(Date from, Date to) {
        return  weeklyReportDao.findDepartmentsWithoutReportsInDateRange(from, to);
    }

    public WeeklyReportEntity getById(int id) {
        return weeklyReportDao.getById(id);
    }

    public boolean removeById(int id) {
        WeeklyReportEntity weeklyReport = weeklyReportDao.getById(id);
        if (weeklyReport == null) {
            throw new NotFoundException("Department is not found");
        }
        weeklyReportDao.delete(weeklyReport);
        return true;
    }

    // User

    public List<WeeklyReportExtends> getCurrentDepartmentReportsByDateRange(Date from, Date to, String username) {
        AccountEntity account = accountDao.getByUsername(username);
        // Save metadata
        DepartmentEntity department = account != null ? account.getDepartment() : null;
        List<WeeklyReportDto> list = getByDepartmentAndDateRange(from, to, department.getId());
        List<WeeklyReportExtends> results = new ArrayList<>();
        list.forEach(weeklyReport -> {
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            // Chuyển Date -> LocalDate theo Zone VN
            LocalDate localDate = weeklyReport.getUploadedAt().toInstant().atZone(zoneId).toLocalDate();
            // Lấy ngày đầu tuần (Thứ 2) và cuối tuần (Chủ Nhật)
            LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            // Nếu bạn muốn trả lại kiểu java.util.Date:
            Date startDate = Date.from(startOfWeek.atStartOfDay(zoneId).toInstant());
            Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(zoneId).toInstant());
//            System.out.println("Ngày gốc: " + weeklyReportEntity.getUploadedAt());
//            System.out.println("Đầu tuần: " + startDate);
//            System.out.println("Cuối tuần: " + endDate);
            // Lấy báo cáo của đơn vị tổng hợp trong tuần hiện tại mà báo cáo của đơn vị nghiệp vụ được chọn
            WeeklyReportEntity rs = weeklyReportDao.getSingleByDepartmentIdFromTo(startDate, endDate, 2);
            WeeklyReportExtends temp = new WeeklyReportExtends();
            temp.setId(weeklyReport.getId());
            temp.setUrl(weeklyReport.getUrl());
            temp.setFilename(weeklyReport.getFilename());
            temp.setOriginName(weeklyReport.getOriginName());
            temp.setDepartment(weeklyReport.getDepartment());
            temp.setUploadedAt(weeklyReport.getUploadedAt());
            if (rs != null) {
                temp.setOriginNameReportEx(rs.getOriginName());
                temp.setDateReportEx(rs.getUploadedAt());
                temp.setUrlReportEx(rs.getUrl());
                temp.setFilenameReportEx(rs.getFilename());
            }
            results.add(temp);
        });
        return results;
    }

    public List<WeeklyReportExtends> getSummaryReportsByDateRange(Date from, Date to, String username) {
        AccountEntity account = accountDao.getByUsername(username);
        // Save metadata
        DepartmentEntity department = account != null ? account.getDepartment() : null;
        List<WeeklyReportDto> list = getByDepartmentAndDateRange(from, to, 2);
        List<WeeklyReportExtends> results = new ArrayList<>();
        list.forEach(weeklyReport -> {
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            // Chuyển Date -> LocalDate theo Zone VN
            LocalDate localDate = weeklyReport.getUploadedAt().toInstant().atZone(zoneId).toLocalDate();
            // Lấy ngày đầu tuần (Thứ 2) và cuối tuần (Chủ Nhật)
            LocalDate startOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            // Nếu bạn muốn trả lại kiểu java.util.Date:
            Date startDate = Date.from(startOfWeek.atStartOfDay(zoneId).toInstant());
            Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(zoneId).toInstant());
            // Lấy báo cáo của đơn vị nghiệp vụ trong tuần hiện tại mà báo cáo tổng hợp được chọn
            WeeklyReportEntity rs = weeklyReportDao.getSingleByDepartmentIdFromTo(startDate, endDate, department.getId());
            WeeklyReportExtends temp = new WeeklyReportExtends();
            temp.setId(weeklyReport.getId());
            temp.setUrl(weeklyReport.getUrl());
            temp.setFilename(weeklyReport.getFilename());
            temp.setOriginName(weeklyReport.getOriginName());
            temp.setDepartment(weeklyReport.getDepartment());
            temp.setUploadedAt(weeklyReport.getUploadedAt());
            if (rs != null) {
                temp.setOriginNameReportEx(rs.getOriginName());
                temp.setDateReportEx(rs.getUploadedAt());
                temp.setUrlReportEx(rs.getUrl());
                temp.setFilenameReportEx(rs.getFilename());
            }
            results.add(temp);
        });
        return results;
    }

    public List<WeeklyReportDto> getByDepartmentAndDateRange(Date from, Date to, Integer departmentId) {
        List<Object[]> rows = weeklyReportDao.getByDepartmentAndDateRange(from, to, departmentId);
        Map<Long, WeeklyReportDto> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Long weekReportId = ((Number) row[0]).longValue();
            String filename = (String) row[1];
            String originName = (String) row[2];
            String url = (String) row[3];
            Date updatedAt = (Date) row[4];
            Integer departmentId_ = ((Number) row[5]).intValue();
            String departmentName = (String) row[6];
            DepartmentDto departmentDto = new DepartmentDto();
            departmentDto.setId(departmentId_);
            departmentDto.setName(departmentName);
            map.computeIfAbsent(weekReportId, id -> new WeeklyReportDto(id, filename, originName, url, updatedAt, departmentDto));
        }
        return new ArrayList<>(map.values());
    }

}
