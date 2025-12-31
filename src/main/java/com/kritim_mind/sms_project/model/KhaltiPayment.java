package com.kritim_mind.sms_project.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhaltiPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String pidx;

    private String status;
    private Integer amount;
    private String transactionId;

    private String purchaseOrderId;
    private String purchaseOrderName;

    private String mobile;
    private String email;

    private LocalDateTime paidAt;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
