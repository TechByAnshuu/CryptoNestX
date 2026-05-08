DO $$
DECLARE
  v_portfolio_id uuid;
  v_user_id uuid := '123e4567-e89b-12d3-a456-426614174000';
BEGIN
  -- Check if portfolio already exists
  SELECT id INTO v_portfolio_id FROM portfolios WHERE user_id = v_user_id;

  -- Create if not exists
  IF v_portfolio_id IS NULL THEN
    v_portfolio_id := gen_random_uuid();
    INSERT INTO portfolios (id, user_id, total_value, created_at, updated_at)
    VALUES (v_portfolio_id, v_user_id, 0, now(), now());
  END IF;

  -- Insert dummy holdings if they don't exist
  IF NOT EXISTS (SELECT 1 FROM holdings WHERE portfolio_id = v_portfolio_id AND symbol = 'BTC') THEN
    INSERT INTO holdings (id, portfolio_id, symbol, quantity, avg_buy_price, created_at, updated_at)
    VALUES (gen_random_uuid(), v_portfolio_id, 'BTC', 1.5, 60000.00, now(), now());
  END IF;

  IF NOT EXISTS (SELECT 1 FROM holdings WHERE portfolio_id = v_portfolio_id AND symbol = 'ETH') THEN
    INSERT INTO holdings (id, portfolio_id, symbol, quantity, avg_buy_price, created_at, updated_at)
    VALUES (gen_random_uuid(), v_portfolio_id, 'ETH', 10.0, 3000.00, now(), now());
  END IF;

  IF NOT EXISTS (SELECT 1 FROM holdings WHERE portfolio_id = v_portfolio_id AND symbol = 'SOL') THEN
    INSERT INTO holdings (id, portfolio_id, symbol, quantity, avg_buy_price, created_at, updated_at)
    VALUES (gen_random_uuid(), v_portfolio_id, 'SOL', 100.0, 100.00, now(), now());
  END IF;

END $$;
