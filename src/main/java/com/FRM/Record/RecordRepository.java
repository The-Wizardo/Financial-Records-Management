package com.FRM.Record;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RecordRepository extends JpaRepository<Record,Long> {
    List<RecordRequest> findByUser_UserIdAndIsDeleted(Long userId, boolean deleted);
}
