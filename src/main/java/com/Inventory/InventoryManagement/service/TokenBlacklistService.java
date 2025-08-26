package com.Inventory.InventoryManagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TokenBlacklistService {
    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void blacklist(String token,Long expiry) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "true", expiry, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        StopWatch sw = new StopWatch();
        logger.info("Stop watch started!");
        sw.start();
        String key = BLACKLIST_PREFIX + token;
        boolean x= redisTemplate.hasKey(key);
        sw.stop();
        logger.info("Stop watch ended!");
        logger.info("Redis lookup time (ns): " + sw.getLastTaskTimeNanos());
        return x;
    }
}
