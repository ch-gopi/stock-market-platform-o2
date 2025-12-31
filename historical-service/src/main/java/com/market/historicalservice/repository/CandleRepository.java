package com.market.historicalservice.repository;

import com.market.historicalservice.dto.CandleDto;
import com.market.historicalservice.entity.CandleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public interface CandleRepository extends JpaRepository<CandleEntity, Long> {


        @Query(value = """
        SELECT DISTINCT ON (day)
            symbol,
            day * 1000 AS day_ts,
            open,
            high,
            low,
            close,
            volume
        FROM (
            SELECT
                symbol,
                (timestamp / 1000 / 86400) * 86400 AS day,
                FIRST_VALUE(open) OVER w AS open,
                MAX(high)         OVER w AS high,
                MIN(low)          OVER w AS low,
                LAST_VALUE(close) OVER w AS close,
                SUM(volume)       OVER w AS volume
            FROM candles
            WHERE symbol = :symbol
              AND timestamp BETWEEN :startTs AND :endTs
            WINDOW w AS (
                PARTITION BY (timestamp / 1000 / 86400)
                ORDER BY timestamp
                ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
            )
        ) AS t
        ORDER BY day DESC
        """,
                nativeQuery = true
        )
        List<Object[]> findDailyCandles(
                @Param("symbol") String symbol,
                @Param("startTs") long startTs,
                @Param("endTs") long endTs
        );
    }




/*

@Repository
public class CandleRepository {
    private final Map<String, List<CandleDto>> store = new ConcurrentHashMap<>();

    public void save(String symbol, CandleDto candle) {
        store.computeIfAbsent(symbol, k -> new ArrayList<>()).add(candle);
    }
    public List<CandleDto> findBySymbolAndRange(String symbol, String range) {
        List<CandleDto> all = store.getOrDefault(symbol, List.of());
        long cutoff = resolveCutoff(range);

        return all.stream()
                .filter(c -> c.getTimestamp() >= cutoff)
                .toList();
    }
    private long resolveCutoff(String range) {
        long now = System.currentTimeMillis();
        return switch (range) {
            case "1d" -> now - Duration.ofDays(1).toMillis();
            case "5d" -> now - Duration.ofDays(5).toMillis();
            case "1mo" -> now - Duration.ofDays(30).toMillis();
            default -> now - Duration.ofDays(10).toMillis();
        };
    }
    private long alignToMinute(long timestamp) {
        return timestamp - (timestamp % 60000); // floor to minute
    }


}
*/
