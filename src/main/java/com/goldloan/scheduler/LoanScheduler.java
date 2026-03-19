package com.goldloan.scheduler;
import com.goldloan.entity.*;
import com.goldloan.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor
public class LoanScheduler {
    private final LoanRepository loanRepository;
    private final AuditLogRepository auditLogRepository;
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void markOverdueLoans() {
        log.info("Running overdue loan marker job");
        List<Loan> loans = loanRepository.findAllActiveOverdue(LocalDate.now());
        for (Loan loan : loans) {
            loan.setStatus(LoanStatus.OVERDUE);
            loanRepository.save(loan);
            auditLogRepository.save(AuditLog.builder()
                    .entityType("LOAN").entityId(loan.getId().toString()).action("AUTO_OVERDUE").build());
        }
        log.info("Marked {} loans as overdue", loans.size());
    }
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void accrueInterest() {
        log.info("Running interest accrual job");
        List<Loan> loans = loanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, LocalDate.now().plusYears(10));
        for (Loan loan : loans) {
            BigDecimal dailyRate = loan.getInterestRate().divide(BigDecimal.valueOf(3000), 10, RoundingMode.HALF_UP);
            long interest = dailyRate.multiply(BigDecimal.valueOf(loan.getOutstandingAmountPaise())).longValue();
            loan.setOutstandingAmountPaise(loan.getOutstandingAmountPaise() + interest);
            loanRepository.save(loan);
        }
        log.info("Accrued interest on {} loans", loans.size());
    }
}
