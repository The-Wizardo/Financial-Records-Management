package com.FRM.Record;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordResponse(
        Long id,
        BigDecimal amount,
        RecordType type,
        String category,
        LocalDate date,
        String note,
        Boolean isDeleted
) {
}
