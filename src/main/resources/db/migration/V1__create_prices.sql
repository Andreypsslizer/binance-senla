CREATE TABLE prices (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    price NUMERIC(30, 12) NOT NULL CHECK (price > 0),
    fetched_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_prices_symbol_fetched_at ON prices (symbol, fetched_at DESC, id DESC);
