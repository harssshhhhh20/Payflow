package com.harsh.payflow.audit.service;

import com.harsh.payflow.audit.entity.AuditEntityType;
import com.harsh.payflow.audit.entity.AuditEventType;

public interface AuditService {

    void record(
            String entityId,
            AuditEntityType entityType,
            AuditEventType eventType,
            String eventData
    );

}