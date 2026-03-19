package com.goldloan.dto.report;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    private Long totalActiveLoans;
    private Long totalOverdueLoans;
    private Long totalClosedLoans;
    private Long totalOutstandingAmountPaise;
    private Long todayCollectionsPaise;
    private Long overdueCount30Days;
    private Long overdueCount60Days;
    private Long overdueCount90Days;
}
