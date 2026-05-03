package com.market.marketsearchservice.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock_meta")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StockMeta {

    @Id
    @EqualsAndHashCode.Include
    @Column(nullable = false, unique = true, length = 10)
    private String symbol;

    private String name;
    private String type;
    private String region;
    private String marketOpen;
    private String marketClose;
    private String timezone;
    private String currency;

    private double matchScore;
    private double price;
    private double change;
    private double changePercent;
    private long volume;
    private double historicalPerformance;
}
