package com.santander.address.api.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "zipcode_service_logs")
public class ZipCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @Column(name = "service_name", nullable = false, length = 50)
    private String serviceName;

    @Lob
    @Column(name = "request", nullable = false, columnDefinition = "TEXT")
    private String request;

    @Lob
    @Column(name = "external_response", nullable = false, columnDefinition = "TEXT")
    private String externalResponse;

    @Lob
    @Column(name = "response", nullable = false, columnDefinition = "TEXT")
    private String response;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
