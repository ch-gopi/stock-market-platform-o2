package com.market.historicalservice.entity;


import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "candles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "timestamp"}),
        indexes = {
            @Index(name = "idx_symbol_timestamp", columnList = "symbol, timestamp")
        }
)
public class CandleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", length = 20, nullable = false)
    private String symbol;

    @Column(name = "timestamp", nullable = false)
    private long timestamp; // millis since epoch

    @Column(name = "open", nullable = false, precision = 19, scale = 8)
    private BigDecimal open;

    @Column(name = "high", nullable = false, precision = 19, scale = 8)
    private BigDecimal high;

    @Column(name = "low", nullable = false, precision = 19, scale = 8)
    private BigDecimal low;

    @Column(name = "close", nullable = false, precision = 19, scale = 8)
    private BigDecimal close;

    @Column(name = "volume", nullable = false, precision = 19, scale = 8)
    private BigDecimal volume;

    public CandleEntity(String symbol,
                        long timestamp,
                        BigDecimal open,
                        BigDecimal high,
                        BigDecimal low,
                        BigDecimal close,
                        BigDecimal volume) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CandleEntity)) return false;
        CandleEntity that = (CandleEntity) o;
        return timestamp == that.timestamp &&
               Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, timestamp);
    }
}
