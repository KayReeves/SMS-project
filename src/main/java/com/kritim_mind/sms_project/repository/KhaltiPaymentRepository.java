package com.kritim_mind.sms_project.repository;

import com.kritim_mind.sms_project.model.KhaltiPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KhaltiPaymentRepository
        extends JpaRepository<KhaltiPayment, Long> {

    Optional<KhaltiPayment> findByPidx(String pidx);
}
