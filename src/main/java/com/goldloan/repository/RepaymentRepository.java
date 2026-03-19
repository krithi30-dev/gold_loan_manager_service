package com.goldloan.repository;
import com.goldloan.entity.Repayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
public interface RepaymentRepository extends JpaRepository<Repayment, UUID> {
    Page<Repayment> findByLoanId(UUID loanId, Pageable pageable);
    @Query("SELECT COALESCE(SUM(r.amountPaise), 0) FROM Repayment r WHERE r.loan.branch.id = :branchId AND r.paymentDate BETWEEN :from AND :to")
    Long sumByBranchAndDateRange(@Param("branchId") UUID branchId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
    @Query("SELECT MAX(r.receiptNumber) FROM Repayment r WHERE r.receiptNumber LIKE 'RCP%'")
    Optional<String> findMaxReceiptNumber();
}
