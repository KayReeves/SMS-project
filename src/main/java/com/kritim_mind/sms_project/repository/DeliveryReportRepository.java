package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.DeliveryReport;
import com.kritim_mind.sms_project.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryReportRepository extends JpaRepository<DeliveryReport, Long> {
    List<DeliveryReport> findByMessageRecipientId(Long messageRecipientId);

    List<DeliveryReport> findByStatus(DeliveryStatus status);

    List<DeliveryReport> findByMessageRecipientIdAndStatus(
            Long messageRecipientId,
            DeliveryStatus status
    );

    @Query("""
    SELECT
        SUM(m.totalSmsParts),
        SUM(CASE WHEN dr.status = com.kritim_mind.sms_project.model.DeliveryStatus.DELIVERED THEN 1 ELSE 0 END),
        SUM(CASE WHEN dr.status = com.kritim_mind.sms_project.model.DeliveryStatus.FAILED THEN 1 ELSE 0 END),
        SUM(CASE WHEN dr.status = com.kritim_mind.sms_project.model.DeliveryStatus.PENDING THEN 1 ELSE 0 END)
    FROM DeliveryReport dr
    JOIN dr.messageRecipient mr
    JOIN mr.message m
    WHERE m.sender.id = :adminId
""")
    Object[] getAllDeliveryStatusAndTotalSms(@Param("adminId") Long adminId);

}
