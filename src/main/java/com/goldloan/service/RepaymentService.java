package com.goldloan.service;
import com.goldloan.dto.repayment.*;
import com.goldloan.entity.*;
import com.goldloan.exception.*;
import com.goldloan.repository.*;
import com.goldloan.util.PDFGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;
@Service
@RequiredArgsConstructor
@Transactional
public class RepaymentService {
    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final PDFGenerator pdfGenerator;
    public RepaymentResponse record(RepaymentRequest req, String email) {
        Loan loan = loanRepository.findById(UUID.fromString(req.getLoanId()))
                .orElseThrow(() -> new ResourceNotFoundException("Loan", req.getLoanId()));
        if (loan.getStatus() == LoanStatus.CLOSED || loan.getStatus() == LoanStatus.RELEASED)
            throw new BusinessException("LOAN_CLOSED", "Cannot repay a closed loan");

        // Calculate total due with minimum 30-day interest
        long interestPaise = LoanService.calculateInterestPaise(
                loan.getOutstandingAmountPaise(), loan.getInterestRate(), loan.getCreatedAt());
        long totalDuePaise = loan.getOutstandingAmountPaise() + interestPaise;

        if (req.getAmountPaise() > totalDuePaise)
            throw new BusinessException("EXCESS_PAYMENT",
                    "Payment ₹" + req.getAmountPaise() / 100 + " exceeds total due ₹" + totalDuePaise / 100
                    + " (principal + interest)");

        UserEntity u = userRepository.findByEmail(email).orElse(null);
        Repayment r = Repayment.builder().loan(loan).amountPaise(req.getAmountPaise())
                .paymentDate(LocalDateTime.now()).paymentMode(PaymentMode.valueOf(req.getPaymentMode()))
                .receiptNumber(generateReceiptNumber()).recordedBy(u).notes(req.getNotes()).build();
        r = repaymentRepository.save(r);

        // Full payment (covers total due) → close loan; partial → reduce principal only
        if (req.getAmountPaise() >= totalDuePaise) {
            loan.setOutstandingAmountPaise(0L);
            loan.setStatus(LoanStatus.CLOSED);
            loan.setClosedAt(LocalDateTime.now());
            if (loan.getJewelleryItems() != null) loan.getJewelleryItems().forEach(i -> i.setStatus("RELEASED"));
        } else {
            loan.setOutstandingAmountPaise(loan.getOutstandingAmountPaise() - req.getAmountPaise());
        }
        loanRepository.save(loan);
        auditLogRepository.save(AuditLog.builder().entityType("REPAYMENT").entityId(r.getId().toString())
                .action("CREATE").performedBy(u).build());
        return toResponse(r);
    }
    @Transactional(readOnly = true)
    public Page<RepaymentResponse> getByLoan(UUID loanId, Pageable pageable) {
        return repaymentRepository.findByLoanId(loanId, pageable).map(this::toResponse);
    }
    public byte[] getReceiptPdf(UUID id) {
        return pdfGenerator.generateRepaymentReceipt(repaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Repayment", id.toString())));
    }
    private String generateReceiptNumber() {
        return repaymentRepository.findMaxReceiptNumber().map(max -> {
            int n = Integer.parseInt(max.replace("RCP", "")) + 1;
            return String.format("RCP%08d", n);
        }).orElse("RCP00000001");
    }
    private RepaymentResponse toResponse(Repayment r) {
        return RepaymentResponse.builder().id(r.getId()).loanId(r.getLoan().getId().toString())
                .loanNumber(r.getLoan().getLoanNumber()).amountPaise(r.getAmountPaise())
                .paymentDate(r.getPaymentDate()).paymentMode(r.getPaymentMode().name())
                .receiptNumber(r.getReceiptNumber()).notes(r.getNotes()).createdAt(r.getCreatedAt()).build();
    }
}
