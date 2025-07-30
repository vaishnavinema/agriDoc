package com.agridoc.repository;

import com.agridoc.entity.DiagnosisRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiagnosisRecordRepository extends JpaRepository<DiagnosisRecord, Long> {
    List<DiagnosisRecord> findByUserIdOrderByDiagnosedAtDesc(Long userId);
}
