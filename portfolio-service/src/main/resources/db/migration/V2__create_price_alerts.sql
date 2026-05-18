-- V2: Price Alerts
-- Migration: portfolio-service

CREATE TABLE price_alert (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID NOT NULL,
    coin_id       VARCHAR(50) NOT NULL,
    coin_symbol   VARCHAR(10) NOT NULL,
    target_price  NUMERIC(18,2) NOT NULL,
    direction     VARCHAR(5) NOT NULL CHECK (direction IN ('ABOVE','BELOW')),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    triggered_at  TIMESTAMPTZ,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
