package txu.report.mainapp.service;

import com.amazonaws.AmazonServiceException;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import txu.report.mainapp.dao.AccountDao;
import txu.report.mainapp.dao.DepartmentDao;
import txu.report.mainapp.dto.Account2Dto;
import txu.report.mainapp.dto.AccountDto;
import txu.report.mainapp.dto.DepartmentDto;
import txu.report.mainapp.dto.LinkDto;
import txu.report.mainapp.entity.AccountEntity;
import txu.common.exception.BadParameterException;
import txu.common.exception.ConflictException;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;
import txu.report.mainapp.entity.DepartmentEntity;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountDao accountDao;
    private final DepartmentDao departmentDao;

    @Value("${ceph.rgw.endpoint}")
    private String url;

    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${ceph.rgw.bucket2}")
    private String bucketName;

    // Upload
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

    @Transactional
    public AccountEntity createOrUpdate(AccountEntity accountEntity) throws NoSuchMethodException {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // Add new
        if (accountEntity.getId() == null || accountEntity.getId() == 0) {
            if (accountEntity.getUsername() == null || accountEntity.getUsername().isEmpty()) {
                throw new BadParameterException("Username is required");
            }
            if (accountEntity.getPassword() == null || accountEntity.getPassword().isEmpty()) {
                throw new BadParameterException("Password is required");
            }
            if (accountEntity.getEmail() == null || accountEntity.getEmail().isEmpty()) {
                throw new BadParameterException("Email is required");
            }
            if (accountEntity.getLastName() == null || accountEntity.getLastName().isEmpty()) {
                throw new BadParameterException("Last Name is required");
            }
            if (accountEntity.getFirstName() == null || accountEntity.getFirstName().isEmpty()) {
                throw new BadParameterException("First Name is required");
            }
            if (accountDao.getByUsername(accountEntity.getUsername()) != null) {
                throw new ConflictException("Account with [" + accountEntity.getUsername() + "]  already exists");
            }
            if (accountDao.getByEmail(accountEntity.getEmail()) != null) {
                throw new ConflictException("Account with [" + accountEntity.getEmail() + "]  already exists");
            }
            if (departmentDao.getById(accountEntity.getDepartment().getId()) == null) {
                throw new NotFoundException("Department not found");
            }
            if (accountEntity.getPassword() != null && !accountEntity.getPassword().isEmpty()) {
                accountEntity.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
            }
            accountEntity.setCreatedAt(DateTime.now().toDate());
            accountEntity.setUpdatedAt(DateTime.now().toDate());
            try {
                accountDao.save(accountEntity);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException(ex.getMessage());
            }
            return accountEntity;
        }

        // Update
        AccountEntity account = accountDao.getById(accountEntity.getId());
        if (account != null) {
            if (accountEntity.getDepartment() != null && accountEntity.getDepartment().getId() != null && accountEntity.getDepartment().getId() != 0) {
                if (departmentDao.getById(accountEntity.getDepartment().getId()) == null){
                    throw new NotFoundException("Department not found");
                }
                // Nếu có đặt lại đơn vị thì cập nhật, không thì bỏ qua (giữ đơn vị cũ)
                account.setDepartment(accountEntity.getDepartment());
            }
            if (accountEntity.getPassword() != null && !accountEntity.getPassword().isEmpty()) {
                account.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
            }
            if (accountEntity.getLastName() != null && !accountEntity.getLastName().isEmpty()) {
                account.setLastName(accountEntity.getLastName());
            }
            if (accountEntity.getFirstName() != null && !accountEntity.getFirstName().isEmpty()) {
                account.setFirstName(accountEntity.getFirstName());
            }
            if (accountEntity.getPhoneNumber() != null && !accountEntity.getPhoneNumber().isEmpty()) {
                account.setPhoneNumber(accountEntity.getPhoneNumber());
            }
            if (accountEntity.getAvatarUrl() != null && !accountEntity.getAvatarUrl().isEmpty()) {
                account.setAvatarUrl(accountEntity.getAvatarUrl());
            }
            if (accountEntity.getAvatarFilename() != null && !accountEntity.getAvatarFilename().isEmpty()) {
                account.setAvatarFilename(accountEntity.getAvatarFilename());
            }
            account.setUpdatedAt(DateTime.now().toDate());
            try {
                return accountDao.save(account);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save account");
            }
        } else {
            throw new NotFoundException("Account not found");
        }
    }

    //    @Transactional
    public Account2Dto getByUsername(String username) {
        Account2Dto user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }
        return user;
    }

    public List<AccountDto> getPaging(long keyOffset, int limit, String keySearch) {
        // Giới hạn limit tối đa là 100 record.
        if (limit > 100 || limit <= 0) limit = 100;
        if (keySearch != null && !keySearch.isEmpty()) {
            keyOffset = 0; // Chế độ tìm kiếm, tìm tất cả.
        }
        List<Object[]> rows = accountDao.getPaging(keyOffset, limit, keySearch);
        Map<Long, AccountDto> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Long accountId = ((Number) row[0]).longValue();
            String username = (String) row[1];
            String firstName = (String) row[2];
            String lastName = (String) row[3];
            Date createdAt = (Date) row[4];
            Date updatedAt = (Date) row[5];
            Long departmentId = ((Number) row[6]).longValue();
            String departmentName = (String) row[7];
            DepartmentDto departmentDto = new DepartmentDto();
            departmentDto.setId(departmentId);
            departmentDto.setName(departmentName);
            // tạo department nếu chưa có
            AccountDto accountDto = map.computeIfAbsent(accountId, id -> new AccountDto(id, username, firstName, lastName, createdAt, updatedAt, departmentDto));
        }
        List<AccountDto> rs = new ArrayList<>(map.values());
        return rs;
    }

    public boolean removeByUsername(String username) {
        Account2Dto account = accountDao.getByUsername(username);
        if (account == null) {
            throw new NotFoundException("User is not found");
        }
        accountDao.delete(accountDao.getById(account.getId()));
        return true;
    }

    public AccountEntity updateAvatar(String filename, String username, String password, String firstName,
                                      String lastName, String email, String phoneNumber) throws NoSuchMethodException {
        Account2Dto account = accountDao.getByUsername(username);
        DepartmentEntity department = new DepartmentEntity();
        department.setId(account.getDepartment().getId());
        AccountEntity accountToUpdate = new AccountEntity();
        accountToUpdate.setId(account.getId());
        accountToUpdate.setDepartment(department);
        if (!StringUtil.isNullOrEmpty(filename)) {
            // Xóa tập tin hình ảnh cũ của người dùng trên storage2 (nếu có) trước khi cập nhật nội dung mới trong csdl
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(account.getAvatarFilename())
                        .build()
                );
                System.out.println("Deleted successfully: " + account.getAvatarFilename());
            } catch (AmazonServiceException e) {
                System.out.println("AWS Service error when deleting object. " + e);
            } catch (SdkClientException e) {
                System.out.println("AWS SDK client error when deleting object " + e);
            }
            String fileUrl = String.format(url + "/%s/%s", bucketName, filename);
            accountToUpdate.setAvatarUrl(fileUrl);
            accountToUpdate.setAvatarFilename(filename);
        }
        // Chỉ cập nhật password, firstName, lastName, email, phoneNumber; avatarUrl, avataFilename nếu tồn tại file avatar
        if (password != null && !password.isEmpty()) {
            accountToUpdate.setPassword(password);
        }
        if (lastName != null && !lastName.isEmpty()) {
            accountToUpdate.setLastName(lastName);
        }
        if (firstName != null && !firstName.isEmpty()) {
            accountToUpdate.setFirstName(firstName);
        }
        if (email != null && !email.isEmpty()) {
            accountToUpdate.setEmail(email);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            accountToUpdate.setPhoneNumber(phoneNumber);
        }
        return createOrUpdate(accountToUpdate);
    }

//    public AccountEntity getCurrentUser() {
//        // Lấy thông tin người dùng gửi request thông qua token, mà lớp filter đã thực hiện qua lưu vào Security context holder
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        AccountEntity account;
//        if (authentication != null && authentication.isAuthenticated()) {
//            Object principal = authentication.getPrincipal();
//            if (principal instanceof CustomUserDetails userDetails) {
//                account = getByUsername(userDetails.getUsername());
//            } else {
//                account = null;
//            }
//        } else {
//            account = null;
//        }
//        return account;
//
//    }
}
