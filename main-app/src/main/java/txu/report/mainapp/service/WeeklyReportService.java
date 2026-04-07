package txu.report.mainapp.service;

import com.amazonaws.AmazonServiceException;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
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
import txu.report.mainapp.dto.Department1Dto;
import txu.report.mainapp.dto.DepartmentDto;

import txu.report.mainapp.dto.LinkDto;
import txu.report.mainapp.dto.UploadfileInfoRequest;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.DepartmentEntity;
import txu.report.mainapp.entity.WeeklyReportEntity;
import txu.common.exception.NotFoundException;

import java.time.Duration;
import java.util.*;

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

    // ✅ UPLOAD
    public LinkDto getPreSignedUrlForPut(String key) {

        LinkDto linkDto = new LinkDto();
        String filename = UUID.randomUUID() + "_" + key;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(2))
                        .putObjectRequest(objectRequest)
                        .build();

        String pre_signed_url = presigner.presignPutObject(presignRequest).url().toString();
        linkDto.setPre_signed_url(pre_signed_url);
        linkDto.setFilename(filename);
        return linkDto;
    }

    // ✅ DOWNLOAD
    public String getPreSignedUrlForGet(String key) {

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest =
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .getObjectRequest(getRequest)
                        .build();

        String pre_signed_url = presigner.presignGetObject(presignRequest).url().toString();
        return pre_signed_url;
    }


    public WeeklyReportEntity addReport(UploadfileInfoRequest request, String username) throws Exception {

        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder.
        // Việc lấy thông tin này ch yếu để xác định người dùng hiện tại đang ở phòng ban nào, để cập nhật hoặc tạo báo cáo cho phòng ban đó.
        // Ở đây không xử lý xác thực người dung, vì việc này đã được thực hiện bở kong gateway
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        CustomUserDetails userDetails;
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof CustomUserDetails) {
//                userDetails = (CustomUserDetails) principal;
////                String username = userDetails.getUsername();
////                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
//            } else {
//                userDetails = null;
//            }
//        } else {
//            userDetails = null;
//        }


        AccountEntity account = accountDao.getById(accountDao.getByUsername(username).getId());

        // Nếu tồn tại những thông tin report trong tuần mà liên qua đến người dùng (thuộc phòng ban) đã upload report hiện tại thì
        // xóa hết report đã upload trên lên storage1 (ngoại trừ file báo cáo hiện tại), và xóa tất cả dữ liệu lưu ở cơ sở dữ liệu (trong tuần hiện tại)
        List<WeeklyReportEntity> weeklyReportEntities = weeklyReportDao.getFromDateToDate(toDate(getStartOfWeek()), toDate(getEndOfWeek()));
        weeklyReportEntities.forEach(weeklyReportEntity -> {
//            if (weeklyReportEntity.getDepartment().getId() == userDetails.getDepartment_id()) {
            if (Objects.equals(weeklyReportEntity.getDepartment().getId(), account.getDepartment().getId())) {

                if (!Objects.equals(weeklyReportEntity.getFilename(), request.getFilename())) {

                    try {
                        s3Client.deleteObject(DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(weeklyReportEntity.getFilename())
                                .build()
                        );
                        System.out.println("Deleted successfully: " + weeklyReportEntity.getFilename());
                    } catch (AmazonServiceException e) {
                        System.out.println("AWS Service error when deleting object. " + e);
                    } catch (SdkClientException e) {
                        System.out.println("AWS SDK client error when deleting object " + e);
                    }
                }
                // Xóa dữ liệu
                weeklyReportDao.delete(weeklyReportEntity);
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


    public List<WeeklyReportEntity> getWithLimit(int limit) {
        return weeklyReportDao.getWithLimit(limit);
    }

    public List<WeeklyReportEntity> getFromDateToDate(Date from, Date to) {
        return weeklyReportDao.getFromDateToDate(from, to);
    }

    public List<DepartmentDto> getNoReportedFromDateToDate(Date from, Date to) {
        List<DepartmentDto> departmentNoReport = new ArrayList<DepartmentDto>();
        ArrayList<Object> departmentsReport = new ArrayList<>();

        List<WeeklyReportEntity> uploadFileEntities = weeklyReportDao.getFromDateToDate(from, to);
        uploadFileEntities.forEach(weeklyReportEntity -> {
            departmentsReport.add(weeklyReportEntity.getDepartment().getId());
        });

        List<Department1Dto> department1Dtos = departmentDao.getPaging(1,100,"");
        department1Dtos.forEach(department -> {
            if (!departmentsReport.contains(department.getId())) {
                DepartmentDto dpm = new DepartmentDto();
                dpm.setId(department.getId());
                dpm.setName(department.getName());
                departmentNoReport.add(dpm);
            }
        });
        return departmentNoReport;
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


}
