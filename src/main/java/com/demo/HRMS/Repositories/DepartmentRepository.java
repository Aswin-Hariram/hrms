package com.demo.HRMS.Repositories;


import com.demo.HRMS.Entities.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity,Long> {

    boolean existsByDepartmentName(String departmentName);

}
