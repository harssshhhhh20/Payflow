package com.harsh.payflow.audit.service;

import com.harsh.payflow.audit.entity.AuditEntityType;
import com.harsh.payflow.audit.entity.AuditEvent;
import com.harsh.payflow.audit.entity.AuditEventType;
import com.harsh.payflow.audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Override
    @Transactional
    public void record(
            String entityId,
            AuditEntityType entityType,
            AuditEventType eventType,
            String eventData
    ) {

        AuditEvent auditEvent = AuditEvent.builder()
                .entityId(entityId)
                .entityType(entityType.name())
                .eventType(eventType)
                .eventData(eventData)
                .build();

        auditRepository.save(auditEvent);
    }
}