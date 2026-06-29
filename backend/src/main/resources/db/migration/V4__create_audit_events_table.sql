CREATE TABLE audit_events (

    id UUID PRIMARY KEY,

    entity_id VARCHAR(100) NOT NULL,

    entity_type VARCHAR(50) NOT NULL,

    event_type VARCHAR(100) NOT NULL,

    event_data TEXT,

    created_at TIMESTAMP NOT NULL

);