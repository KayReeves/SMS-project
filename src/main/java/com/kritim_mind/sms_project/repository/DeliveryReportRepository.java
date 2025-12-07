package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.DeliveryReport;
import com.kritim_mind.sms_project.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryReportRepository extends JpaRepository<DeliveryReport, Long> {
    List<DeliveryReport> findByMessageRecipientId(Long messageRecipientId);

    List<DeliveryReport> findByStatus(DeliveryStatus status);

    List<DeliveryReport> findByMessageRecipientIdAndStatus(
            Long messageRecipientId,
            DeliveryStatus status
    );
}
