package txu.report.mainapp.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.dao.AccountDao;
import txu.report.mainapp.dao.DepartmentDao;
import txu.report.mainapp.entity.AccountEntity;
import txu.common.exception.BadParameterException;
import txu.common.exception.ConflictException;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountDao accountDao;
    private final DepartmentDao departmentDao;

    @Value("${ceph.rgw.bucket}")
    private String bucketName;

    @Value("${ceph.rgw.endpoint}")
    private String url;

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

            if (accountDao.getByUsername(accountEntity.getUsername()) != null) {
                throw new ConflictException("Account with [" + accountEntity.getUsername() + "]  already exists");
            }

            if (accountDao.getByEmail(accountEntity.getEmail()) != null) {
                throw new ConflictException("Account with [" + accountEntity.getEmail() + "]  already exists");
            }

            if (departmentDao.findById(accountEntity.getDepartment().getId()) == null) {
                throw new NotFoundException("Department not found");
            }

//            if (roleDao.findById(accountEntity.getRole().getId()) == null) {
//                throw new NotFoundException("Role not found");
//            }

            if (accountEntity.getPassword() != null && !accountEntity.getPassword().isEmpty()) {
                accountEntity.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
            }
            accountEntity.setCreatedAt(DateTime.now().toDate());
            accountEntity.setUpdateAt(DateTime.now().toDate());
            AccountEntity account = null;

            try {
                account = accountDao.save(accountEntity);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException(ex.getMessage());
            }
            return account;
        }

        // Update
        AccountEntity account = accountDao.findById(accountEntity.getId());

        if (account != null) {

            if (accountDao.getByEmail(accountEntity.getEmail()) != null && !account.getEmail().equals(accountEntity.getEmail())) {
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

            account.setUpdateAt(DateTime.now().toDate());

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
//    public AccountEntity updateAvatar(
//            MultipartFile file,
//            String username,
//            String password,
//            String firstName,
//            String lastName,
//            String email,
//            String phoneNumber
//    ) throws  IOException, NoSuchAlgorithmException, InvalidKeyException {
//
//        AccountEntity account = getByUsername(username);
//        AccountEntity accountToUpdate = new AccountEntity();
//
//        if (file != null && !file.isEmpty()) {
//            // Xóa avatar hiện tại của account
//            try {
//                minioClient.removeObject(
//                        RemoveObjectArgs.builder()
//                                .bucket(bucketName)
//                                .object(account.getAvatarFilename())
//                                .build()
//                );
//                System.out.println("Deleted successfully: " + account.getAvatarFilename());
//            } catch (Exception e) {
//                System.err.println("Error deleting file: " + e.getMessage());
//            }
//
//            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            String fileUrl = String.format(url + "/%s/%s", bucketName, filename);
//            accountToUpdate.setAvatarUrl(fileUrl);
//            accountToUpdate.setAvatarFilename(filename);
//
//            // Ensure bucket exists
//            boolean found;
//            try {
//                found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//            if (!found) {
//                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
//            }
//
//            // Tạo file avatar mới
//            minioClient.putObject(
//                    PutObjectArgs.builder()
//                            .bucket(bucketName)
//                            .object(filename)
//                            .stream(file.getInputStream(), file.getSize(), -1)
//                            .contentType(file.getContentType())
//                            .build()
//            );
//        }
//
//        // Chỉ cập nhật password, firstName, lastName, email, phoneNumber; avatarUrl, avataFilename nếu tồn tại file avatar
//
//        if (password != null && !password.isEmpty()) {
//            accountToUpdate.setPassword(password);
//        }
//        accountToUpdate.setId(account.getId());
//        if (lastName != null && !lastName.isEmpty()) {
//            accountToUpdate.setLastName(lastName);
//        }
//        if (firstName != null && !firstName.isEmpty()) {
//            accountToUpdate.setFirstName(firstName);
//        }
//        if (email != null && !email.isEmpty()) {
//            accountToUpdate.setEmail(email);
//        }
//        if (phoneNumber != null && !phoneNumber.isEmpty()) {
//            accountToUpdate.setPhoneNumber(phoneNumber);
//        }
//
//        return createOrUpdate(accountToUpdate);
//
//    }

    //    @Transactional
    public AccountEntity getByUsername(String username) {
        AccountEntity user = accountDao.getByUsername(username);
        if (user == null) {
            throw new NotFoundException("User is not found");
        }
        return user;
    }

    public List<AccountEntity> getWithLimit(int limit) {
        return accountDao.getWithLimit(limit);
    }

    public boolean removeByUsername(String username) {
        AccountEntity account = accountDao.getByUsername(username);
        if (account == null) {
            throw new NotFoundException("User is not found");
        }
        accountDao.remove(account);
        return true;
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
