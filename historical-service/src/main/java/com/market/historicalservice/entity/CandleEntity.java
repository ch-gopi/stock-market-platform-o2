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
        uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "timestamp"})
)

public class CandleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "symbol", length = 20, nullable = false)
    private String symbol;

    @Column(name = "timestamp", nullable = false) // explicit mapping
    private long timestamp;                        // millis since epoch

    @Column(name = "open", nullable = false)
    private double open;

    @Column(name = "high", nullable = false)
    private double high;

    @Column(name = "low", nullable = false)
    private double low;

    @Column(name = "close", nullable = false)
    private double close;

    @Column(name = "volume", nullable = false)
    private double volume;
    public CandleEntity(String symbol,
                        long timestamp,
                        double open,
                        double high,
                        double low,
                        double close,
                        double volume) {
        this.symbol = symbol;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
    // getters/setters
}
