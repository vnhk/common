package com.bervan.logging;

import com.bervan.history.model.BaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface LogRepository extends BaseRepository<LogEntity, Long> {

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM logs_owners
            WHERE log_entity_id IN (
                SELECT id FROM logs WHERE timestamp < :cutoff
            )
            """, nativeQuery = true)
    void deleteOwnersByOldLogs(@Param("cutoff") LocalDateTime cutoff);

    @Modifying
    @Transactional
    @Query("DELETE FROM LogEntity l WHERE l.timestamp < :cutoff")
    void deleteOldLogs(@Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT DISTINCT applicationName FROM LogEntity")
    Set<String> findAllApplicationNames();

    @Query("SELECT DISTINCT processName FROM LogEntity")
    List<String> findAllProcessNames();

    @Query("SELECT DISTINCT moduleName FROM LogEntity")
    List<String> findAllModulesNames();

    @Query("SELECT l FROM LogEntity l WHERE l.applicationName = :appName " +
           "AND (:fromTime IS NULL OR l.timestamp >= :fromTime) " +
           "AND (:toTime IS NULL OR l.timestamp <= :toTime) " +
           "AND (:logLevel IS NULL OR l.logLevel = :logLevel) " +
           "AND (:processName IS NULL OR l.processName = :processName) " +
           "AND (:moduleName IS NULL OR l.moduleName = :moduleName)")
    Page<LogEntity> findLogs(
            @Param("appName") String appName,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime,
            @Param("logLevel") String logLevel,
            @Param("processName") String processName,
            @Param("moduleName") String moduleName,
            Pageable pageable);

    @Query("SELECT l FROM LogEntity l WHERE l.applicationName = :appName " +
           "AND (:processName IS NULL OR l.processName = :processName) " +
           "AND (:moduleName IS NULL OR l.moduleName = :moduleName) " +
           "AND (:className IS NULL OR LOWER(l.className) LIKE LOWER(CONCAT('%', :className, '%'))) " +
           "AND (:methodName IS NULL OR LOWER(l.methodName) LIKE LOWER(CONCAT('%', :methodName, '%'))) " +
           "AND (:message IS NULL OR LOWER(l.message) LIKE LOWER(CONCAT('%', :message, '%')))")
    Page<LogEntity> findTrackers(
            @Param("appName") String appName,
            @Param("processName") String processName,
            @Param("moduleName") String moduleName,
            @Param("className") String className,
            @Param("methodName") String methodName,
            @Param("message") String message,
            Pageable pageable);

    @Query("SELECT l FROM LogEntity l WHERE l.applicationName = :appName " +
           "AND (:fromTime IS NULL OR l.timestamp >= :fromTime) " +
           "AND (:toTime IS NULL OR l.timestamp <= :toTime) " +
           "AND (:logLevel IS NULL OR l.logLevel = :logLevel) " +
           "AND (:processName IS NULL OR l.processName = :processName) " +
           "AND (:moduleName IS NULL OR l.moduleName = :moduleName) " +
           "ORDER BY l.timestamp ASC")
    List<LogEntity> findAllForExport(
            @Param("appName") String appName,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime,
            @Param("logLevel") String logLevel,
            @Param("processName") String processName,
            @Param("moduleName") String moduleName);
}

