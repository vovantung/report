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
import txu.report.mainapp.dto.Account2Dto;
import txu.report.mainapp.dto.AccountDto;
import txu.report.mainapp.dto.DepartmentDto;
import txu.report.mainapp.entity.AccountEntity;
import txu.common.exception.BadParameterException;
import txu.common.exception.ConflictException;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;

import java.util.*;

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

            if (departmentDao.getById(accountEntity.getDepartment().getId()) == null) {
                throw new NotFoundException("Department not found");
            }

            if (accountEntity.getPassword() != null && !accountEntity.getPassword().isEmpty()) {
                accountEntity.setPassword(bCryptPasswordEncoder.encode(accountEntity.getPassword()));
            }
            accountEntity.setCreatedAt(DateTime.now().toDate());
            accountEntity.setUpdatedAt(DateTime.now().toDate());
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
        AccountEntity account = accountDao.getById(accountEntity.getId());

        if (account != null) {

            if (accountDao.getByEmail(accountEntity.getEmail()) != null && !account.getEmail().equals(accountEntity.getEmail())) {
                throw new ConflictException("Account with [" + accountEntity.getEmail() + "]  already exists");
            }
            if (accountEntity.getDepartment() != null
                    && accountEntity.getDepartment().getId() != null
                    && accountEntity.getDepartment().getId() != 0
                    && departmentDao.getById(accountEntity.getDepartment().getId()) != null) {
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
            Integer departmentId = ((Number)row[6]).intValue();
            String departmentName = (String) row[7];
            DepartmentDto departmentDto = new DepartmentDto();
            departmentDto.setId(departmentId);
            departmentDto.setName(departmentName);
            // tạo department nếu chưa có
            AccountDto department = map.computeIfAbsent(accountId, id -> new AccountDto(id, username, firstName, lastName,createdAt,updatedAt, departmentDto));
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
