package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
    Page<Message> findBySenderId(Long senderId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE " +
            "(:senderId IS NULL OR m.sender.id = :senderId) AND " +
            "(:from IS NULL OR m.createdAt >= :from) AND " +
            "(:to IS NULL OR m.createdAt <= :to)")
    Page<Message> findByFilters(
            @Param("senderId") Long senderId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    @Query("SELECT SUM(m.totalSmsParts) FROM Message m WHERE " +
            "m.sender.id = :senderId AND " +
            "m.createdAt >= :startDate AND m.createdAt < :endDate")
    Long sumSmsPartsBySenderAndDateRange(
            @Param("senderId") Long senderId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.sender.id = :senderId AND " +
            "m.createdAt >= :startDate AND m.createdAt < :endDate")
    Long countBySenderAndDateRange(
            @Param("senderId") Long senderId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT DATE(m.createdAt) as date, SUM(m.totalSmsParts) as total " +
            "FROM Message m WHERE m.sender.id = :senderId AND " +
            "m.createdAt >= :startDate AND m.createdAt < :endDate " +
            "GROUP BY DATE(m.createdAt) ORDER BY DATE(m.createdAt)")
    List<Object[]> getDailySmsUsage(
            @Param("senderId") Long senderId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
