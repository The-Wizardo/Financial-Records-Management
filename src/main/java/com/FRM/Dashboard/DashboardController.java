package com.FRM.Dashboard;

import com.FRM.Auth.CustomUserDetails;
import com.FRM.Exception.ApiResponseUtil;
import com.FRM.Record.RecordRepository;
import com.FRM.Record.RecordType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final RecordRepository recordRepository;

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        assert user != null;
        BigDecimal income = recordRepository.getTotalByType(user.getUser().getUserId(), RecordType.INCOME);
        BigDecimal expense = recordRepository.getTotalByType(user.getUser().getUserId(), RecordType.EXPENSE);
        DashboardSummaryResponse response = new DashboardSummaryResponse(income, expense, income.subtract(expense));
        return ResponseEntity.ok(ApiResponseUtil.success("Summary Report", response));
    }


}
