package txu.report.mainapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import txu.report.mainapp.dao.RoleDao;
import txu.report.mainapp.entity.RoleEntity;
import txu.common.exception.BadParameterException;
import txu.common.exception.NotFoundException;
import txu.common.exception.TxException;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleDao roleDao;

    @Transactional
    public RoleEntity createOrUpdate(RoleEntity roleEntity) {

        // Add new
        if (roleEntity.getId() == null || roleEntity.getId() == 0) {
            if (roleEntity.getName() == null || roleEntity.getName().isEmpty()) {
                throw new BadParameterException("Name fied is required");
            }

            roleEntity.setCreatedAt(DateTime.now().toDate());
            roleEntity.setUpdatedAt(DateTime.now().toDate());
            RoleEntity role = null;

            try {
                role = roleDao.save(roleEntity);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save role");
            }
            return role;
        }

        // Update
        RoleEntity role = roleDao.findById(roleEntity.getId());

        if (role != null) {

            if (roleEntity.getName() == null || roleEntity.getName().isEmpty()) {
                throw new BadParameterException("Name fied is required");
            }

            if (roleEntity.getName() != null && !roleEntity.getName().isEmpty()) {
                role.setName(roleEntity.getName());
            }

            role.setUpdatedAt(DateTime.now().toDate());
            try {
                return roleDao.save(role);
            } catch (DataIntegrityViolationException ex) {
                log.warn(ex.getMessage());
                throw new TxException("Cannot save role");
            }
        } else {
            throw new NotFoundException("Role not found");
        }

    }

    public List<RoleEntity> getWithLimit(int limit) {
        return roleDao.getWithLimit(limit);
    }

    public RoleEntity getById(int id) {

        return roleDao.findById(id);
    }

    public boolean removeById(int id) {
        RoleEntity role = roleDao.findById(id);
        if (role == null) {
            throw new NotFoundException("Role is not found");
        }
        roleDao.remove(role);
        return true;
    }
}
