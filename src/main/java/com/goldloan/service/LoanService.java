package com.goldloan.service;
import com.goldloan.dto.loan.*;
import com.goldloan.entity.*;
import com.goldloan.exception.*;
import com.goldloan.repository.*;
import com.goldloan.util.PDFGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
@Service
@RequiredArgsConstructor
@Transactional
public class LoanService {
    private final LoanRepository loanRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final GoldRateService goldRateService;
    private final AuditLogRepository auditLogRepository;
    private final PDFGenerator pdfGenerator;
    public LoanResponse createLoan(LoanRequest req, String email) {
        Customer customer = customerRepository.findById(UUID.fromString(req.getCustomerId()))
                .orElseThrow(() -> new ResourceNotFoundException("Customer", req.getCustomerId()));
        Branch branch = req.getBranchId() != null
                ? branchRepository.findById(UUID.fromString(req.getBranchId())).orElse(null) : null;
        UserEntity executive = userRepository.findByEmail(email).orElse(null);
        long totalGoldValue = 0;
        List<JewelleryItem> items = new ArrayList<>();
        for (LoanRequest.JewelleryItemRequest ir : req.getJewelleryItems()) {
            long rate = goldRateService.getRateForKarat(ir.getPurityKarat());
            long est = (long) (rate * ir.getWeightGrams());
            totalGoldValue += est;
            items.add(JewelleryItem.builder().itemType(ir.getItemType()).weightGrams(ir.getWeightGrams())
                    .purityKarat(ir.getPurityKarat()).estimatedValuePaise(est).imageUrl(ir.getImageUrl()).build());
        }
        int ltv = req.getLtvPercent() != null ? req.getLtvPercent() : 75;
        long loanAmount = (totalGoldValue * ltv) / 100;
        String loanNumber = generateLoanNumber();
        Loan loan = Loan.builder().loanNumber(loanNumber).customer(customer)
                .loanAmountPaise(loanAmount).outstandingAmountPaise(loanAmount)
                .interestRate(req.getInterestRate()).tenureMonths(req.getTenureMonths())
                .status(LoanStatus.ACTIVE).branch(branch).sanctionedBy(executive)
                .dueDate(LocalDate.now().plusMonths(req.getTenureMonths())).build();
        loan = loanRepository.save(loan);
        Loan savedLoan = loan;
        items.forEach(item -> item.setLoan(savedLoan));
        loan.setJewelleryItems(items);
        loan = loanRepository.save(loan);
        auditLogRepository.save(AuditLog.builder().entityType("LOAN").entityId(loan.getId().toString())
                .action("CREATE").performedBy(executive).build());
        return toResponse(loan);
    }
    @Transactional(readOnly = true)
    public LoanResponse getById(UUID id) {
        return toResponse(loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", id.toString())));
    }
    @Transactional(readOnly = true)
    public Page<LoanResponse> getAll(String status, Pageable pageable) {
        if (status != null)
            return loanRepository.findByStatus(LoanStatus.valueOf(status.toUpperCase()), pageable).map(this::toResponse);
        return loanRepository.findAll(pageable).map(this::toResponse);
    }
    @Transactional(readOnly = true)
    public Page<LoanResponse> getByCustomer(UUID customerId, Pageable pageable) {
        return loanRepository.findByCustomerId(customerId, pageable).map(this::toResponse);
    }
    public LoanResponse closeLoan(UUID id, String email) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", id.toString()));
        if (loan.getOutstandingAmountPaise() > 0)
            throw new BusinessException("OUTSTANDING_BALANCE", "Outstanding balance must be zero to close the loan");
        loan.setStatus(LoanStatus.CLOSED);
        loan.setClosedAt(LocalDateTime.now());
        if (loan.getJewelleryItems() != null) loan.getJewelleryItems().forEach(i -> i.setStatus("RELEASED"));
        UserEntity u = userRepository.findByEmail(email).orElse(null);
        auditLogRepository.save(AuditLog.builder().entityType("LOAN").entityId(loan.getId().toString())
                .action("CLOSE").performedBy(u).build());
        return toResponse(loanRepository.save(loan));
    }
    public byte[] getLoanReceiptPdf(UUID id) {
        return pdfGenerator.generateLoanReceipt(loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", id.toString())));
    }
    private String generateLoanNumber() {
        return loanRepository.findMaxLoanNumber().map(max -> {
            int n = Integer.parseInt(max.replace("GL", "")) + 1;
            return String.format("GL%08d", n);
        }).orElse("GL00000001");
    }
    /** Minimum 30-day (1-month) interest rule; rounds up to full months thereafter. */
    public static long calculateInterestPaise(long outstandingPaise, java.math.BigDecimal annualMonthlyRate, LocalDateTime loanCreatedAt) {
        long daysElapsed = ChronoUnit.DAYS.between(loanCreatedAt.toLocalDate(), LocalDate.now());
        long daysForCalc = Math.max(30, daysElapsed);
        long months = (long) Math.ceil(daysForCalc / 30.0);
        return (long) (outstandingPaise * annualMonthlyRate.doubleValue() / 100.0 * months);
    }

    private LoanResponse toResponse(Loan l) {
        long outstanding = l.getOutstandingAmountPaise();
        boolean active = l.getStatus() == LoanStatus.ACTIVE || l.getStatus() == LoanStatus.OVERDUE;
        long interestPaise = active ? calculateInterestPaise(outstanding, l.getInterestRate(), l.getCreatedAt()) : 0L;
        long totalDuePaise = active ? outstanding + interestPaise : 0L;

        return LoanResponse.builder().id(l.getId()).loanNumber(l.getLoanNumber())
                .customerId(l.getCustomer().getId().toString()).customerName(l.getCustomer().getName())
                .customerPhone(l.getCustomer().getPhone()).loanAmountPaise(l.getLoanAmountPaise())
                .outstandingAmountPaise(outstanding).interestAccruedPaise(interestPaise).totalDuePaise(totalDuePaise)
                .interestRate(l.getInterestRate()).tenureMonths(l.getTenureMonths()).status(l.getStatus().name())
                .branchId(l.getBranch() != null ? l.getBranch().getId().toString() : null)
                .branchName(l.getBranch() != null ? l.getBranch().getName() : null)
                .dueDate(l.getDueDate()).closedAt(l.getClosedAt()).createdAt(l.getCreatedAt())
                .jewelleryItems(l.getJewelleryItems() != null ? l.getJewelleryItems().stream()
                        .map(i -> LoanResponse.JewelleryItemResponse.builder().id(i.getId())
                                .itemType(i.getItemType()).weightGrams(i.getWeightGrams())
                                .purityKarat(i.getPurityKarat()).estimatedValuePaise(i.getEstimatedValuePaise())
                                .imageUrl(i.getImageUrl()).status(i.getStatus()).build()).toList() : List.of())
                .build();
    }
}
