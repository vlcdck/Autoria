package com.autoria.models.ad;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ad_views")
@EntityListeners(AuditingEntityListener.class)
@Data
public class AdView {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private CarAd carAd;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    private String region;
}
