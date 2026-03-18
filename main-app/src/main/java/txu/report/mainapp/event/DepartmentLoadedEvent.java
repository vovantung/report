package txu.report.mainapp.event;

import txu.report.mainapp.entity.DepartmentEntity;

public record DepartmentLoadedEvent(int id, DepartmentEntity dept) {}