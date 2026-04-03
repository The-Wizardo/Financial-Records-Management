package com.FRM.Record;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface RecordRepository extends JpaRepository<Record, Long> {
    List<RecordResponse> findByUser_UserIdAndIsDeleted(Long userId, boolean deleted);

    Optional<Record> findByIdAndUser_UserIdAndIsDeletedFalse(Long id, Long userId);
}