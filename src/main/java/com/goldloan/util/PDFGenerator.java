package com.goldloan.util;
import com.goldloan.entity.*;
import org.springframework.stereotype.Component;
@Component
public class PDFGenerator {
    public byte[] generateLoanReceipt(Loan loan) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("         GOLD LOAN MANAGER              \n");
        sb.append("         LOAN PLEDGE RECEIPT            \n");
        sb.append("========================================\n");
        sb.append("Loan Number  : ").append(loan.getLoanNumber()).append("\n");
        sb.append("Customer     : ").append(loan.getCustomer().getName()).append("\n");
        sb.append("Phone        : ").append(loan.getCustomer().getPhone()).append("\n");
        sb.append("Loan Amount  : Rs.").append(loan.getLoanAmountPaise() / 100.0).append("\n");
        sb.append("Interest Rate: ").append(loan.getInterestRate()).append("% per month\n");
        sb.append("Tenure       : ").append(loan.getTenureMonths()).append(" months\n");
        sb.append("Due Date     : ").append(loan.getDueDate()).append("\n");
        sb.append("Status       : ").append(loan.getStatus()).append("\n");
        sb.append("========================================\n");
        if (loan.getJewelleryItems() != null) {
            sb.append("Jewellery Items:\n");
            loan.getJewelleryItems().forEach(item -> sb.append("  - ")
                    .append(item.getItemType()).append(" | ").append(item.getWeightGrams()).append("g")
                    .append(" | ").append(item.getPurityKarat()).append("K")
                    .append(" | Rs.").append(item.getEstimatedValuePaise() / 100.0).append("\n"));
        }
        sb.append("========================================\n");
        return sb.toString().getBytes();
    }
    public byte[] generateRepaymentReceipt(Repayment repayment) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("       GOLD LOAN MANAGER                \n");
        sb.append("       REPAYMENT RECEIPT                \n");
        sb.append("========================================\n");
        sb.append("Receipt No   : ").append(repayment.getReceiptNumber()).append("\n");
        sb.append("Loan Number  : ").append(repayment.getLoan().getLoanNumber()).append("\n");
        sb.append("Amount Paid  : Rs.").append(repayment.getAmountPaise() / 100.0).append("\n");
        sb.append("Payment Mode : ").append(repayment.getPaymentMode()).append("\n");
        sb.append("Payment Date : ").append(repayment.getPaymentDate()).append("\n");
        sb.append("========================================\n");
        return sb.toString().getBytes();
    }
}
