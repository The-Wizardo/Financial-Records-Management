package com.FRM.Record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecordRequest(
        @NotNull
        @Positive
        BigDecimal amount,

        @NotNull
        RecordType type,

        @NotBlank
        String category,

        @NotNull
        LocalDate date,

        String note
) {
}
