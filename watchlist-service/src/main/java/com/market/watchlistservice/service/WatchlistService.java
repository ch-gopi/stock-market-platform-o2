package com.market.watchlistservice.service;

import com.market.watchlistservice.client.QuotesClient;
import com.market.watchlistservice.dto.*;
import com.market.watchlistservice.entity.WatchlistEntry;
import com.market.common.dto.FinQuoteTickEvent;
import com.market.watchlistservice.repository.WatchlistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class WatchlistService {

    private final WatchlistRepository repo;
    private final QuoteCacheService quoteCache;
    private final HistoricalDataService historicalDataService;
    private final QuotesClient quotesClient;

    public WatchlistService(WatchlistRepository repo,
                            QuoteCacheService quoteCache,
                            HistoricalDataService historicalDataService,
                            QuotesClient quotesClient) {
        this.repo = repo;
        this.quoteCache = quoteCache;
        this.historicalDataService = historicalDataService;
        this.quotesClient = quotesClient;
    }

    public List<WatchlistItemDto> getUserWatchlist(Long userId) {
        return repo.findByUserId(userId).stream()
                .map(item -> buildDto(item.getSymbol()))
                .toList();
    }

    public WatchlistItemDto addToWatchlist(Long userId, String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("symbol must not be null or blank");
        }
        String normalized = symbol.toUpperCase();

        repo.findByUserIdAndSymbolIgnoreCase(userId, normalized)
                .orElseGet(() -> {
                    WatchlistEntry entry = new WatchlistEntry();
                    entry.setUserId(userId);
                    entry.setSymbol(normalized);
                    repo.save(entry);
                    return entry;
                });
        log.info("Watchlist entry added to watchlist");

        //  Always return enriched DTO
        return buildDto(normalized);
    }

    public void removeFromWatchlist(Long userId, String symbol) {
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("symbol must not be null or blank");
        }
        repo.findByUserId(userId).stream()
                .filter(i -> i.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .ifPresent(repo::delete);
        log.info("Watchlist entry removed from watchlist");
    }

    /** Helper to build a WatchlistItemDto with live quote and sparkline */
    private WatchlistItemDto buildDto(String symbol) {
        FinQuoteTickEvent tick = quoteCache.getLatestTick(symbol);
        double prevClose = quoteCache.getPreviousClose(symbol);

        // Circuit breaker applied here (delegated to a separate bean so the
        // @CircuitBreaker proxy is actually invoked - see HistoricalDataService)
        List<CandleDto> history = historicalDataService.getHistory(symbol);
        List<Double> sparkline = history.stream()
                .map(CandleDto::getClose)
                .toList();

        if (tick != null) {
            double lastPrice = tick.getPrice();
            double change = lastPrice - prevClose;
            double changePercent = prevClose != 0 ? (change / prevClose) * 100 : 0;

            return new WatchlistItemDto(
                    symbol,
                    lastPrice,
                    change,
                    changePercent,
                    sparkline
            );
        }

        // No live WebSocket tick cached yet (e.g. market closed, or the symbol just
        // hasn't traded since quotes-service started). QuotesClient was previously
        // defined but never actually called anywhere, so watchlist entries always
        // showed 0.0 outside of active market hours. Fall back to the on-demand
        // REST quote (same data source your curl test against Finnhub confirmed
        // works and returns a value even when markets are closed).
        try {
            QuoteDto quote = quotesClient.getQuote(symbol);
            if (quote != null) {
                return new WatchlistItemDto(
                        symbol,
                        quote.getPrice(),
                        quote.getChange(),
                        quote.getChangePercent(),
                        sparkline
                );
            }
        } catch (Exception e) {
            log.warn("Fallback quote lookup via quotes-service failed for {}: {}", symbol, e.getMessage());
        }

        return new WatchlistItemDto(symbol, 0, 0, 0, sparkline);
    }
}
