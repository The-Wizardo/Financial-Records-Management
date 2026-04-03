package com.FRM.Record;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface RecordRepository extends JpaRepository<Record, Long> {
    List<RecordResponse> findByUser_UserIdAndIsDeleted(Long userId, boolean deleted);

    Optional<Record> findByIdAndUser_UserIdAndIsDeletedFalse(Long id, Long userId);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM Record r
            WHERE r.user.userId = :userId
            AND r.type = :type
            AND r.isDeleted = false
            """)
    BigDecimal getTotalByType(Long userId, RecordType type);

    @Query("""
             SELECT r.category, SUM(r.amount)
            FROM Record r
            WHERE r.user.userId = :userId
            AND r.isDeleted = false
            GROUP BY r.category
            """)
    List<Object[]> getCategorySummary(Long userId);

}