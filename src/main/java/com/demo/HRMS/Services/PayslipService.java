package com.demo.HRMS.Services;

import com.demo.HRMS.DTO.Payslip.*;
import com.demo.HRMS.Entities.EmployeeCompensationEntity;
import com.demo.HRMS.Entities.EmployeeEntity;
import com.demo.HRMS.Entities.PayslipEntity;
import com.demo.HRMS.Repositories.*;
import com.demo.HRMS.Services.Payroll.DefaultSalaryCalculator;
import com.demo.HRMS.Types.EmployeeStatus;
import com.demo.HRMS.Types.LeaveRequestStatus;
import com.demo.HRMS.Types.PayslipStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayslipService{

    private final EmployeeRepository employeeRepository;
    private final EmployeeCompensationRepository compensationRepository;
    private final PayslipRepository payslipRepository;
    private final DepartmentRepository departmentRepository;
    private final DefaultSalaryCalculator salaryCalculator;
    private final LeaveRequestRepository leaveRequestRepository;



    @Transactional
    public PayslipResponse generateForEmployee(Long orgId, Long empId,
                                               LocalDate periodStart,
                                               LocalDate periodEnd) {

        EmployeeEntity employee = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException(
                        "Employee " + empId + " not found in organisation " + orgId));

        return generateFor(employee, periodStart, periodEnd);
    }



    @Transactional
    public BulkPayslipResponse generateBulk(BulkPayslipRequest request) {

        Long orgId = request.getOrgId();
        LocalDate periodEnd = request.getPeriodEnd() != null
                ? request.getPeriodEnd() : LocalDate.now();

        List<EmployeeEntity> employees;

        if (request.getDepartmentId() != null) {
            departmentRepository.findByDepartmentIdAndOrganisation_OrgID(
                            request.getDepartmentId(), orgId)
                    .orElseThrow(() -> new RuntimeException(
                            "Department " + request.getDepartmentId()
                                    + " does not belong to organisation " + orgId));

            employees = employeeRepository
                    .findByOrganisation_OrgIDAndDepartment_DepartmentId(
                            orgId, request.getDepartmentId());
        } else {
            employees = employeeRepository.findByOrganisation_OrgID(orgId);
        }

        List<PayslipResponse> generated = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int success = 0, skipped = 0, failed = 0;

        for (EmployeeEntity emp : employees) {
            try {
                if (emp.getEmpStatus() != EmployeeStatus.ACTIVE) {
                    skipped++;
                    continue;
                }

                LocalDate start = request.getPeriodStart() != null
                        ? request.getPeriodStart()
                        : (emp.getLastSalaryPaidDate() != null
                        ? emp.getLastSalaryPaidDate().plusDays(1)
                        : emp.getEmpJoiningDate());

                generated.add(generateFor(emp, start, periodEnd));
                success++;

            } catch (Exception ex) {
                failed++;
                errors.add("Emp " + emp.getEmpID() + ": " + ex.getMessage());

            }
        }

        return BulkPayslipResponse.builder()
                .totalRequested(employees.size())
                .successCount(success)
                .skippedCount(skipped)
                .failedCount(failed)
                .payslips(generated)
                .errors(errors)
                .build();
    }



    public List<PayslipResponse> getPayslipsForEmployee(Long orgId, Long empId) {

        employeeRepository.findByEmpIDAndOrganisation_OrgID(empId, orgId)
                .orElseThrow(() -> new RuntimeException(
                        "Employee " + empId + " not found in organisation " + orgId));

        return payslipRepository
                .findByEmployee_EmpIDAndOrganisation_OrgIDOrderByPeriodEndDesc(empId, orgId)
                .stream().map(this::toResponse).toList();
    }


    public PayslipResponse getPayslip(Long orgId, Long payslipId) {
        PayslipEntity p = payslipRepository
                .findByPayslipIdAndOrganisation_OrgID(payslipId, orgId)
                .orElseThrow(() -> new RuntimeException(
                        "Payslip " + payslipId + " not found in organisation " + orgId));
        return toResponse(p);
    }


    private PayslipResponse generateFor(EmployeeEntity employee, LocalDate periodStart, LocalDate periodEnd) {

        Long orgId = employee.getOrganisation().getOrgID();

        if (periodStart == null) {
            periodStart = employee.getLastSalaryPaidDate() != null
                    ? employee.getLastSalaryPaidDate().plusDays(1)
                    : employee.getEmpJoiningDate();
        }

        if (periodEnd.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Period end cannot be in the future");
        }

        if (periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException(
                    "Nothing to pay — lastSalaryPaidDate is already at or after " + periodEnd);
        }


        payslipRepository.findByEmployee_EmpIDAndOrganisation_OrgIDAndPeriodStartAndPeriodEnd(employee.getEmpID(), orgId, periodStart, periodEnd)
                .ifPresent(p -> {
                    throw new IllegalStateException(
                            "Payslip already exists for this period: " + p.getPayslipId());
                });

        EmployeeCompensationEntity comp = compensationRepository
                .findFirstByEmployee_EmpIDAndEmployee_Organisation_OrgIDAndActiveTrueOrderByEffectiveFromDesc(
                        employee.getEmpID(), orgId)
                .orElseThrow(() -> new IllegalStateException(
                        "No active compensation found for emp " + employee.getEmpID()));

        int leaveday = leaveRequestRepository.getTotalLeaveDays(
                employee.getOrganisation().getOrgID(),
                employee.getEmpID(),
                LeaveRequestStatus.APPROVED,
                periodStart,
                periodEnd,
                false
        );
        PayslipEntity payslip = salaryCalculator.calculate(
                employee, comp, periodStart, periodEnd,leaveday);



        payslip.setUnpaidLeaveDays(leaveday);

        if (payslip.getPaidDays() <= 0) {
            throw new IllegalStateException("No payable days in period");
        }

        PayslipEntity saved = payslipRepository.save(payslip);


        employeeRepository.save(employee);

        return toResponse(saved);
    }

    private PayslipResponse toResponse(PayslipEntity p) {
        return PayslipResponse.builder()
                .payslipId(p.getPayslipId())
                .orgId(p.getOrganisation() != null ? p.getOrganisation().getOrgID() : null)
                .empID(p.getEmployee().getEmpID())
                .employeeName(p.getEmployee().getEmpFirstName() + " "
                        + p.getEmployee().getEmpLastName())
                .employeeEmail(p.getEmployee().getEmpEmail())
                .departmentName(p.getDepartment() != null
                        ? p.getDepartment().getDepartmentName() : null)
                .organisationName(p.getOrganisation() != null
                        ? p.getOrganisation().getOrgName() : null)
                .employmentType(p.getEmploymentType())
                .payType(p.getPayType())
                .unPaidLeaveTaken(p.getUnpaidLeaveDays())
                .status(p.getStatus())
                .periodStart(p.getPeriodStart())
                .periodEnd(p.getPeriodEnd())
                .totalDays(p.getTotalDays())
                .paidDays(p.getPaidDays())
                .basicSalary(p.getBasicSalary())
                .hra(p.getHra())
                .stipend(p.getStipend())
                .hourlyRate(p.getHourlyRate())
                .hourlyHours(p.getHourlyHours())
                .grossPay(p.getGrossPay())
                .pfPercentage(p.getPfPercentage())
                .pfAmount(p.getPfAmount())
                .netPay(p.getNetPay())
                .generatedAt(p.getGeneratedAt())
                .build();
    }

    private List<PayslipEntity> resolveTargets(ApproveRequest req, Long orgId) {

        if (req.getPayslipIds() != null && !req.getPayslipIds().isEmpty()) {
            return payslipRepository.findAllById(req.getPayslipIds())
                    .stream()
                    .filter(p -> p.getOrganisation() != null
                            && Objects.equals(p.getOrganisation().getOrgID(), orgId))
                    .toList();
        }

        if (req.getDepartmentId() != null) {
            return payslipRepository
                    .findByOrganisation_OrgIDAndDepartment_DepartmentIdAndStatus(
                            orgId, req.getDepartmentId(), PayslipStatus.SUBMITTED);
        }

        if (req.getPeriodFrom() != null && req.getPeriodTo() != null) {
            return payslipRepository
                    .findByOrganisation_OrgIDAndStatusAndPeriodEndBetween(
                            orgId, PayslipStatus.SUBMITTED,
                            req.getPeriodFrom(), req.getPeriodTo());
        }


        return payslipRepository.findByOrganisation_OrgIDAndStatus(
                orgId, PayslipStatus.SUBMITTED);
    }

    public List<PayslipResponse> listByStatus(Long orgId,
                                              PayslipStatus status,
                                              LocalDate from,
                                              LocalDate to) {

        if (from != null && to != null) {
            return payslipRepository
                    .findByOrganisation_OrgIDAndStatusAndPeriodEndBetween(
                            orgId, status, from, to)
                    .stream().map(this::toResponse).toList();
        }
        return payslipRepository
                .findByOrganisation_OrgIDAndStatus(orgId, status)
                .stream().map(this::toResponse).toList();
    }

    public List<PayslipResponse> getAllPayslip(Long orgId) {


        return payslipRepository.findByOrganisation_OrgID(orgId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public PayslipActionResponse approve(ApproveRequest request, Long callerId, Long orgId) {

        EmployeeEntity caller = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(callerId, orgId)
                .orElseThrow(() -> new RuntimeException("Caller not found"));



        List<PayslipEntity> targets = resolveTargets(request, orgId);

        List<Long> succeededIds = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int skipped = 0;

        for (PayslipEntity p : targets) {
            try {
                if (p.getStatus() != PayslipStatus.SUBMITTED) {
                    skipped++;
                    continue;
                }
                p.setStatus(PayslipStatus.APPROVED);
                p.setApprovedByEmpId(caller.getEmpID());
                p.setApprovedAt(LocalDateTime.now());
                payslipRepository.save(p);
                succeededIds.add(p.getPayslipId());
            } catch (Exception ex) {
                errors.add("Payslip " + p.getPayslipId() + ": " + ex.getMessage());
            }
        }

        return PayslipActionResponse.builder()
                .requested(targets.size())
                .succeeded(succeededIds.size())
                .skipped(skipped)
                .failed(errors.size())
                .succeededIds(succeededIds)
                .errors(errors)
                .build();
    }


    @Transactional
    public PayslipActionResponse pay(PayRequest request, Long callerId, Long orgId) {

        EmployeeEntity caller = employeeRepository
                .findByEmpIDAndOrganisation_OrgID(callerId, orgId)
                .orElseThrow(() -> new RuntimeException("Caller not found"));



        List<PayslipEntity> targets = payslipRepository
                .findAllById(request.getPayslipIds())
                .stream()
                .filter(p -> p.getOrganisation() != null
                        && Objects.equals(p.getOrganisation().getOrgID(), orgId))
                .toList();

        List<Long> succeededIds = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int skipped = 0;

        for (PayslipEntity p : targets) {
            try {
                if (p.getStatus() != PayslipStatus.APPROVED) {
                    skipped++;
                    continue;
                }

                p.setStatus(PayslipStatus.PAID);
                p.setPaidByEmpId(callerId);
                p.setPaidAt(LocalDateTime.now());
                p.setPaymentMode(request.getPaymentMode());
                p.setPaymentReference(request.getPaymentReference());
                payslipRepository.save(p);


                EmployeeEntity emp = p.getEmployee();
                LocalDate newLast = emp.getLastSalaryPaidDate() == null
                        ? p.getPeriodEnd()
                        : (p.getPeriodEnd().isAfter(emp.getLastSalaryPaidDate())
                        ? p.getPeriodEnd()
                        : emp.getLastSalaryPaidDate());
                emp.setLastSalaryPaidDate(newLast);
                employeeRepository.save(emp);

                succeededIds.add(p.getPayslipId());
            } catch (Exception ex) {
                errors.add("Payslip " + p.getPayslipId() + ": " + ex.getMessage());
            }
        }

        return PayslipActionResponse.builder()
                .requested(request.getPayslipIds().size())
                .succeeded(succeededIds.size())
                .skipped(skipped)
                .failed(errors.size())
                .succeededIds(succeededIds)
                .errors(errors)
                .build();
    }
}