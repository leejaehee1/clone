package com.jsoftware.platform.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@Log4j2
public class RedisUtil {
    public static Set<String> scan(RedisTemplate<String, Object> redisTemplate, String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            try (Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(pattern).count(100).build())) {
                while (cursor.hasNext()) {
                    keysTmp.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
                cursor.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }

            return keysTmp;
        });
    }
}
