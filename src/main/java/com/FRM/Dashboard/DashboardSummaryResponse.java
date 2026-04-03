package com.FRM.Dashboard;

import java.math.BigDecimal;

public record DashboardSummaryResponse (
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
){
}
