-- V2: Transaction Ledger — immutable audit trail of every trade
-- Migration: exchange-service

CREATE TABLE transaction_ledger (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID        NOT NULL,
    type            VARCHAR(10) NOT NULL CHECK (type IN ('BUY', 'SELL')),
    coin_id         VARCHAR(50) NOT NULL,
    coin_symbol     VARCHAR(10) NOT NULL,
    quantity        NUMERIC(18, 8) NOT NULL,
    price_at_trade  NUMERIC(18, 2) NOT NULL,
    total_value     NUMERIC(18, 2) NOT NULL,
    fee             NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
    status          VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ledger_user_id    ON transaction_ledger (user_id);
CREATE INDEX idx_ledger_created_at ON transaction_ledger (created_at DESC);
