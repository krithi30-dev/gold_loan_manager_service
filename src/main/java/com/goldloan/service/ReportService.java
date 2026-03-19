package com.goldloan.service;
import com.goldloan.dto.report.DashboardResponse;
import com.goldloan.entity.LoanStatus;
import com.goldloan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    private final LoanRepository loanRepository;
    private final RepaymentRepository repaymentRepository;
    public DashboardResponse getDashboard(UUID branchId) {
        LocalDate today = LocalDate.now();
        return DashboardResponse.builder()
                .totalActiveLoans(loanRepository.countByStatusAndBranchId(LoanStatus.ACTIVE, branchId))
                .totalOverdueLoans(loanRepository.countByStatusAndBranchId(LoanStatus.OVERDUE, branchId))
                .totalClosedLoans(loanRepository.countByStatusAndBranchId(LoanStatus.CLOSED, branchId))
                .totalOutstandingAmountPaise(loanRepository.sumOutstandingByBranchId(branchId))
                .todayCollectionsPaise(repaymentRepository.sumByBranchAndDateRange(branchId, today.atStartOfDay(), LocalDateTime.now()))
                .overdueCount30Days(loanRepository.countOverdueSince(today.minusDays(30), branchId))
                .overdueCount60Days(loanRepository.countOverdueSince(today.minusDays(60), branchId))
                .overdueCount90Days(loanRepository.countOverdueSince(today.minusDays(90), branchId))
                .build();
    }
}
