package com.demo.HRMS.Repositories;

import com.demo.HRMS.Entities.LeaveSheetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveSheetRepository extends JpaRepository<LeaveSheetEntity,Long> {
}
