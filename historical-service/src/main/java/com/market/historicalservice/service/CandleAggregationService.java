package com.market.historicalservice.service;


import com.market.historicalservice.dto.CandleDto;
import com.market.common.dto.FinQuoteTickEvent;
import com.market.historicalservice.dto.QuoteTickEvent;
import com.market.historicalservice.entity.CandleEntity;
import com.market.historicalservice.repository.CandleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
@Slf4j

public class CandleAggregationService {

    private final CandleRepository candleRepository;

    public CandleAggregationService(CandleRepository candleRepository) {
        this.candleRepository = candleRepository;
    }

    /**
     * Save every tick immediately
     */
    @Transactional
    public void onTick(FinQuoteTickEvent tick) {

        CandleEntity candle = new CandleEntity(
                tick.getSymbol(),
                tick.getTimestamp(), // UTC 그대로
                tick.getPrice(),     // open
                tick.getPrice(),     // high
                tick.getPrice(),     // low
                tick.getPrice(),     // close
                tick.getVolume()
        );

        candleRepository.save(candle);

        log.info("💾 Saved tick as candle [{} @ {}]", tick.getSymbol(), tick.getTimestamp());
    }
}
