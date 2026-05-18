-- V3: Wallet Transactions — deposit/withdrawal simulation
-- Migration: exchange-service

ALTER TABLE wallets ADD COLUMN IF NOT EXISTS currency VARCHAR(10) NOT NULL DEFAULT 'USD';

CREATE TABLE wallet_transaction (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id   UUID        NOT NULL REFERENCES wallets(id),
    type        VARCHAR(10) NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAWAL')),
    amount      NUMERIC(18, 2) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    reference   VARCHAR(100),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_wallet_txn_wallet_id ON wallet_transaction (wallet_id);
CREATE INDEX idx_wallet_txn_created_at ON wallet_transaction (created_at DESC);
