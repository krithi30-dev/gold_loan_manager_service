package com.goldloan.repository;
import com.goldloan.entity.Loan;
import com.goldloan.entity.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Page<Loan> findByCustomerId(UUID customerId, Pageable pageable);
    Page<Loan> findByStatus(LoanStatus status, Pageable pageable);
    Page<Loan> findByBranchIdAndStatus(UUID branchId, LoanStatus status, Pageable pageable);
    Optional<Loan> findByLoanNumber(String loanNumber);
    @Query("SELECT MAX(l.loanNumber) FROM Loan l WHERE l.loanNumber LIKE 'GL%'")
    Optional<String> findMaxLoanNumber();
    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDate date);
    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = :status AND l.branch.id = :branchId")
    Long countByStatusAndBranchId(@Param("status") LoanStatus status, @Param("branchId") UUID branchId);
    @Query("SELECT COALESCE(SUM(l.outstandingAmountPaise), 0) FROM Loan l WHERE l.status IN ('ACTIVE','OVERDUE') AND l.branch.id = :branchId")
    Long sumOutstandingByBranchId(@Param("branchId") UUID branchId);
    @Query(value = "SELECT COUNT(*) FROM loans WHERE status = 'OVERDUE' AND due_date < :cutoff AND branch_id = :branchId", nativeQuery = true)
    Long countOverdueSince(@Param("cutoff") LocalDate cutoff, @Param("branchId") UUID branchId);
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < :today")
    List<Loan> findAllActiveOverdue(@Param("today") LocalDate today);
}
