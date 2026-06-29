package com.harsh.payflow.audit.repository;

import com.harsh.payflow.audit.entity.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditRepository
        extends JpaRepository<AuditEvent, UUID> {
}