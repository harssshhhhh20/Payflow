package com.harsh.payflow.audit.entity;

public enum AuditEventType {

    PAYMENT_CREATED,

    PAYMENT_CAPTURED,

    PAYMENT_FAILED,

    PAYMENT_RETRY_INITIATED,

    PAYMENT_CANCELLED,

    PAYMENT_REFUNDED

}