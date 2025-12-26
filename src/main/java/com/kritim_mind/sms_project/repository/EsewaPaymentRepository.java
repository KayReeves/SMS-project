package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.EsewaPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EsewaPaymentRepository extends JpaRepository<EsewaPayment, Long> {
    Optional<EsewaPayment> findByTransactionUuid(String transactionUuid);
    List<EsewaPayment> findAllByOrderByCreatedAtDesc();
}
