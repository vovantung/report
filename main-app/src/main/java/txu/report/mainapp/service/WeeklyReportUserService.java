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
import txu.report.mainapp.dao.WeeklyReportUserDao;
import txu.report.mainapp.dto.LinkDto;
import txu.report.mainapp.dto.UploadfileInfoRequest;
import txu.report.mainapp.dto.WeeklyReportExtends;
import txu.report.mainapp.entity.AccountEntity;
import txu.report.mainapp.entity.DepartmentEntity;
import txu.report.mainapp.entity.WeeklyReportEntity;
//import txu.user.mainapp.dao.DepartmentDao;
//import txu.user.mainapp.dao.WeeklyReportDao;
//import txu.user.mainapp.dto.LinkDto;
//import txu.user.mainapp.dto.UploadfileInfoRequest;
//import txu.user.mainapp.dto.WeeklyReportExtends;
//import txu.user.mainapp.entity.AccountEntity;
//import txu.user.mainapp.entity.DepartmentEntity;
//import txu.user.mainapp.entity.WeeklyReportEntity;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static txu.report.mainapp.common.DateUtil.*;

@Service
@RequiredArgsConstructor
public class WeeklyReportUserService {
    private final WeeklyReportUserDao weeklyReportDao;
    private final DepartmentDao departmentDao;
    private final AccountService accountService;
    private final S3Client s3Client;
    private final AccountDao accountDao;

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

//        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder.
//        // Việc lấy thông tin này ch yếu để xác định người dùng hiện tại đang ở phòng ban nào, để cập nhật hoặc tạo báo cáo cho phòng ban đó.
//        // Ở đây không xử lý xác thực người dung, vì việc này đã được thực hiện bở kong gateway
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

        AccountEntity account = accountDao.getByUsername(username);

        // Nếu tồn tại những thông tin report trong tuần mà liên qua đến người dùng (thuộc phòng ban) đã upload report hiện tại thì
        // xóa hết report đã upload trên lên storage1 (ngoại trừ file báo cáo hiện tại), và xóa tất cả dữ liệu lưu ở cơ sở dữ liệu (trong tuần hiện tại)
        List<WeeklyReportEntity> weeklyReportEntities = weeklyReportDao.getFromTo(toDate(getStartOfWeek()), toDate(getEndOfWeek()));
        weeklyReportEntities.forEach(weeklyReportEntity -> {
            if (weeklyReportEntity.getDepartment().getId() == account.getDepartment().getId()) {

                if (weeklyReportEntity.getFilename() != request.getFilename()) {

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
                weeklyReportDao.remove(weeklyReportEntity);
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




//    public WeeklyReportEntity create(MultipartFile file) throws Exception {
//
//        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        CustomUserDetails userDetails;
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof CustomUserDetails) {
//                userDetails = (CustomUserDetails) principal;
//            } else {
//                userDetails = null;
//            }
//        } else {
//            userDetails = null;
//        }
//
//        // Nếu tồn tại những thông tin report trong tuần mà liên qua đến người dùng đang upload report hiện tại thì
//        // xóa hết report đã upload trên minio và xóa hết dữ liệu lưu ở cơ sở dữ liệu (trong tuần hiện tại)
//        List<WeeklyReportEntity> weeklyReportEntities = weeklyReportDao.getFromTo(toDate(getStartOfWeek()), toDate(getEndOfWeek()));
//        weeklyReportEntities.forEach(weeklyReportExtends -> {
//            if (weeklyReportExtends.getDepartment().getId() == userDetails.getDepartment_id()) {
//                // Xóa file trên minio
//                try {
//                    minioClient.removeObject(
//                            RemoveObjectArgs.builder()
//                                    .bucket(bucketName)
//                                    .object(weeklyReportExtends.getFilename())
//                                    .build()
//                    );
//                    System.out.println("Deleted successfully: " + weeklyReportExtends.getFilename());
//                } catch (Exception e) {
//                    System.err.println("Error deleting file: " + e.getMessage());
//                    throw new RuntimeException("File deletion failed", e);
//                }
//                // Xóa dữ liệu
//                weeklyReportDao.remove(weeklyReportExtends);
//            }
//        });
//
//        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        // Ensure bucket exists
//        boolean found;
//        try {
//            found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        if (!found) {
//            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//        }
//
//        // Upload to MinIO
//        minioClient.putObject(
//                PutObjectArgs.builder()
//                        .bucket(bucketName)
//                        .object(filename)
//                        .stream(file.getInputStream(), file.getSize(), -1)
//                        .contentType(file.getContentType())
//                        .build()
//        );
//
//        String fileUrl = String.format(url + "/%s/%s", bucketName, filename);
//
//        // Save metadata
//        DepartmentEntity department = null;
//        if (userDetails != null) {
//            department = departmentDao.findById(userDetails.getDepartment_id());
//        }
//
//        WeeklyReportEntity weeklyReport = new WeeklyReportEntity();
//        weeklyReport.setFilename(filename);
//        weeklyReport.setUrl(fileUrl);
//        weeklyReport.setOriginName(file.getOriginalFilename());
//        weeklyReport.setDepartment(department);
//        weeklyReport.setUploadedAt(DateTime.now().toDate());
//        return weeklyReportDao.save(weeklyReport);
//    }

    public List<WeeklyReportExtends> getDepartmentFromTo(Date from, Date to, String username) {
//        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        CustomUserDetails userDetails;
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof CustomUserDetails) {
//                userDetails = (CustomUserDetails) principal;
//            } else {
//                userDetails = null;
//            }
//        } else {
//            userDetails = null;
//        }

        AccountEntity account = accountDao.getByUsername(username);

        // Save metadata
        DepartmentEntity department = null;
        if (account != null) {
            department = departmentDao.getById(account.getDepartment().getId());
        }
        assert department != null;
        List<WeeklyReportEntity> list = weeklyReportDao.getByDepartmentIdFromTo(
                from,
                to,
                department.getId());

        List<WeeklyReportExtends> results = new ArrayList<>();
        list.forEach(weeklyReport -> {

            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

            // Chuyển Date -> LocalDate theo Zone VN
            LocalDate localDate = weeklyReport.getUploadedAt().toInstant()
                    .atZone(zoneId)
                    .toLocalDate();

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

    public List<WeeklyReportExtends> getSummaryReportFromTo(Date from, Date to, String username) {
//        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        CustomUserDetails userDetails;
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof CustomUserDetails) {
//                userDetails = (CustomUserDetails) principal;
//            } else {
//                userDetails = null;
//            }
//        } else {
//            userDetails = null;
//        }

        AccountEntity account = accountDao.getByUsername(username);

        // Save metadata
        DepartmentEntity department;
        if (account != null) {
            department = departmentDao.getById(account.getDepartment().getId());
        } else {
            department = null;
        }
        assert department != null;
        List<WeeklyReportEntity> list = weeklyReportDao.getByDepartmentIdFromTo(
                from,
                to,
                2);
        List<WeeklyReportExtends> results = new ArrayList<>();
        list.forEach(weeklyReport -> {

            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");

            // Chuyển Date -> LocalDate theo Zone VN
            LocalDate localDate = weeklyReport.getUploadedAt().toInstant()
                    .atZone(zoneId)
                    .toLocalDate();

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
}
