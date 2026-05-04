package txu.report.mainapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import txu.report.mainapp.dao.WorkflowDao;
import txu.report.mainapp.entity.WorkFlowEntity;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkflowService {
    private final WorkflowDao workflowDao;

    public List<WorkFlowEntity> get() {
        return workflowDao.get(100);
    }


}
