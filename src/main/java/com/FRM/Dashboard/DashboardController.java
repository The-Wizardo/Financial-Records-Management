package com.FRM.Dashboard;

import com.FRM.Auth.CustomUserDetails;
import com.FRM.Exception.ApiResponse;
import com.FRM.Exception.ApiResponseUtil;
import com.FRM.Record.RecordRepository;
import com.FRM.Record.RecordResponse;
import com.FRM.Record.RecordType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/category")
    public ResponseEntity<?> getCategory(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        assert user != null;

        List<Object[]> result = recordRepository.getCategorySummary(user.getUser().getUserId());

        Map<String, BigDecimal> map = new HashMap<>();

        for (Object[] row : result) {
            map.put((String) row[0], (BigDecimal) row[1]);
        }

        return ResponseEntity.ok(ApiResponseUtil.success("Category wise Record", map));

    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<?>> getRecent(Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        assert user != null;
        List<RecordResponse> data = recordRepository
                .findTop5ByUser_UserIdAndIsDeletedFalseOrderByDateDesc(user.getUser().getUserId())
                .stream()
                .map(r -> new RecordResponse(
                        r.getId(),
                        r.getAmount(),
                        r.getType(),
                        r.getCategory(),
                        r.getDate(),
                        r.getNote(),
                        r.isDeleted()
                ))
                .toList();
        return ResponseEntity.ok(ApiResponseUtil.success("Recent Records", data));
    }

    @GetMapping("/trends")
    public ResponseEntity<ApiResponse<?>> getTrends(@RequestParam(defaultValue = "MONTH") String type, Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        assert user != null;

        List<Object[]> result;

        if ("WEEK".equalsIgnoreCase(type)) {
            result = recordRepository.getWeeklyTrends(user.getUser().getUserId());
        } else {
            result = recordRepository.getMonthlyTrends(user.getUser().getUserId());
        }

        Map<Integer, BigDecimal> data = new LinkedHashMap<>();

        for (Object[] row : result) {
            data.put(((Number) row[0]).intValue(), (BigDecimal) row[1]);
        }

        return ResponseEntity.ok(ApiResponseUtil.success("Trends", data));
    }

}
