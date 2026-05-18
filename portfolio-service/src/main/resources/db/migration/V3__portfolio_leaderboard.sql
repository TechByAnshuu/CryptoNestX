-- V3: Portfolio Leaderboard Support
-- Migration: portfolio-service

ALTER TABLE portfolios 
ADD COLUMN IF NOT EXISTS is_public BOOLEAN NOT NULL DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS username VARCHAR(50),
ADD COLUMN IF NOT EXISTS initial_deposit NUMERIC(19,4) NOT NULL DEFAULT 0.00;

CREATE INDEX idx_portfolio_public ON portfolios (is_public);
