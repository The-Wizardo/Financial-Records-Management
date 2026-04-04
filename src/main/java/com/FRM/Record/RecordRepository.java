package com.FRM.Record;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface RecordRepository extends JpaRepository<Record, Long> {
    List<RecordResponse> findByUser_UserIdAndIsDeleted(Long userId, boolean deleted);

    Optional<Record> findByIdAndIsDeletedFalse(Long id);

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM Record r
            WHERE r.user.userId = :userId
            AND r.type = :type
            AND r.isDeleted = false
            """)
    BigDecimal getTotalByType(Long userId, RecordType type);

    @Query("""
    SELECT r FROM Record r
    WHERE r.isDeleted = false
    AND (:type IS NULL OR r.type = :type)
    AND (
        :search IS NULL OR
        r.category LIKE CONCAT('%', :search, '%') OR
        r.note LIKE CONCAT('%', :search, '%')
    )
    AND (:amount IS NULL OR r.amount = :amount)
""")
    Page<Record> findRecords(
            RecordType type,
            String search,
            BigDecimal amount,
            Pageable pageable
    );

    @Query("""
            SELECT r.category, SUM(r.amount)
            FROM Record r
            WHERE r.user.userId = :userId
            AND r.isDeleted = false
            GROUP BY r.category
            """)
    List<Object[]> getCategorySummary(Long userId);

    List<Record> findTop5ByUser_UserIdAndIsDeletedFalseOrderByDateDesc(Long userId);

    @Query("""
            SELECT FUNCTION('DATE_PART', 'month', r.date), SUM(r.amount)
            FROM Record r
            WHERE r.user.userId = :userId
            AND r.isDeleted = false
            GROUP BY FUNCTION('DATE_PART', 'month', r.date)
            ORDER BY FUNCTION('DATE_PART', 'month', r.date)
            """)
    List<Object[]> getMonthlyTrends(@Param("userId") Long userId);

    @Query("""
            SELECT FUNCTION('DATE_PART', 'week', r.date), SUM(r.amount)
            FROM Record r
            WHERE r.user.userId = :userId
            AND r.isDeleted = false
            GROUP BY FUNCTION('DATE_PART', 'week', r.date)
            ORDER BY FUNCTION('DATE_PART', 'week', r.date)
            """)
    List<Object[]> getWeeklyTrends(@Param("userId") Long userId);
}