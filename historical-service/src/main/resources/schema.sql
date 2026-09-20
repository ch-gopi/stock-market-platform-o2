CREATE TABLE IF NOT EXISTS candles (
    id         BIGSERIAL PRIMARY KEY,
    symbol     VARCHAR(20)      NOT NULL,
    timestamp  BIGINT           NOT NULL,
    open       DOUBLE PRECISION NOT NULL,
    high       DOUBLE PRECISION NOT NULL,
    low        DOUBLE PRECISION NOT NULL,
    close      DOUBLE PRECISION NOT NULL,
    volume     DOUBLE PRECISION NOT NULL,
    CONSTRAINT uk_candles_symbol_timestamp UNIQUE (symbol, timestamp)
);

CREATE INDEX IF NOT EXISTS idx_symbol_timestamp ON candles (symbol, timestamp);
