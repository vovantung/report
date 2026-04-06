package txu.report.mainapp.service;

import com.amazonaws.AmazonServiceException;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import txu.report.mainapp.dao.AccountUserDao;
import txu.report.mainapp.dao.DepartmentDao;
import txu.report.mainapp.dto.LinkDto;
import txu.report.mainapp.entity.AccountEntity;
import txu.common.exception.BadParameterException;
import txu.common.exception.ConflictException;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;
//import txu.user.mainapp.dao.AccountDao;
//import txu.user.mainapp.dao.DepartmentDao;
//import txu.user.mainapp.dao.RoleDao;
//import txu.user.mainapp.dto.LinkDto;
//import txu.user.mainapp.entity.AccountEntity;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountUserService {

    private final AccountUserDao accountUserDao;
    private final DepartmentDao departmentDao;
//    private final RoleDao roleDao;

    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${ceph.rgw.bucket2}")
    private String bucketName;

    @Value("${ceph.rgw.endpoint}")
    private String url;

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
    public AccountEntity createOrUpdate(AccountEntity accountEntity) {

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

            if (accountUserDao.getByUsername(accountEntity.getUsername()) != null) {
                throw new ConflictException("Account with [" + accountEntity.getUsername() + "]  already exists");
            }

            if (accountUserDao.getByEmail(accountEntity.getEmail()) != null) {
                throw new ConflictException("Account with [" + accountEntity.getEmail() + "]  already exists");
            }

            if (departmentDao.findById(accountEntity.getDepartment().getId()) != null) {
                throw new NotFoundException("Department not found");
            }

//            if (roleDao.findById(accountEntity.getRole().getId()) == null) {
//                throw new NotFoundException("Role not found");
//            }

            if (accountEntity.getPassword() != null && !accountEntity.getPassword().isEmpty()) {
                accountEntity.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
            }
            accountEntity.setCreatedAt(DateTime.now().toDate());
            accountEntity.setUpdatedAt(DateTime.now().toDate());
            AccountEntity account = null;

            try {
                account = accountUserDao.save(accountEntity);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save account");
            }
            return account;
        }

        // Update
        AccountEntity account = accountUserDao.findById(accountEntity.getId());

        if (account != null) {

            if (accountUserDao.getByEmail(accountEntity.getEmail()) != null && !account.getEmail().equals(accountEntity.getEmail())) {
                throw new ConflictException("Account with [" + accountEntity.getEmail() + "]  already exists");
            }
            if (accountEntity.getDepartment() != null
                    && accountEntity.getDepartment().getId() != null
                    && accountEntity.getDepartment().getId() != 0
                    && departmentDao.findById(accountEntity.getDepartment().getId()) != null) {
                // Nếu có đặt lại đơn vị thì cập nhật, không thì bỏ qua (giữ đơn vị cũ)
                account.setDepartment(accountEntity.getDepartment());
            }


//            if (accountEntity.getRole() != null
//                    && accountEntity.getRole().getId() != null
//                    && accountEntity.getRole().getId() != 0
//                    && departmentDao.findById(accountEntity.getRole().getId()) != null) {
//                // Nếu có đặt lại role thì cập nhật, không thì bỏ qua (giữ lại role cũ)
//                account.setRole(accountEntity.getRole());
//            }

            if (accountEntity.getPassword() != null && !accountEntity.getPassword().isEmpty()) {
                account.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
            }
            if (accountEntity.getLastName() != null && !accountEntity.getLastName().isEmpty()) {
                account.setLastName(accountEntity.getLastName());
            }
            if (accountEntity.getFirstName() != null && !accountEntity.getFirstName().isEmpty()) {
                account.setFirstName(accountEntity.getFirstName());
            }
            if (accountEntity.getEmail() != null && !accountEntity.getEmail().isEmpty()) {
                account.setEmail(accountEntity.getEmail());
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
                return accountUserDao.save(account);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save account");
            }
        } else {
            throw new NotFoundException("Account not found");
        }
    }

    @Transactional
    public AccountEntity updateAvatar(String filename, String username, String password, String firstName, String lastName,
                                      String email, String phoneNumber) {

        AccountEntity account = getByUsername(username);
        AccountEntity accountToUpdate = new AccountEntity();

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
        accountToUpdate.setId(account.getId());
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

    //    @Transactional
    public AccountEntity getByUsername(String username) {
        AccountEntity user = accountUserDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }
        return user;
    }
}
