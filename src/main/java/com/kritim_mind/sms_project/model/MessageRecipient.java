package com.kritim_mind.sms_project.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "message_recipients", indexes = {
            @Index(name = "idx_recipient_message", columnList = "message_id"),
            @Index(name = "idx_recipient_contact", columnList = "contact_id"),
            @Index(name = "idx_recipient_group", columnList = "group_id"),
            @Index(name = "idx_recipient_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "phone_no", nullable = false, length = 20)
    private String phoneNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MessageStatus status = MessageStatus.PENDING;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @OneToMany(mappedBy = "messageRecipient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DeliveryReport> deliveryReports = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
