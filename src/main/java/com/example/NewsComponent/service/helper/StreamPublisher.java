package com.example.NewsComponent.service.helper;

import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StreamPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    public StreamPublisher(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Async
    public void publish(final String streamId,
                        final Object objectToPublish) {
        StreamOperations<String, String, Object> opsForStream = redisTemplate.opsForStream();
        ObjectRecord<String, Object> record = StreamRecords
                .newRecord()
                .ofObject(objectToPublish)
                .withStreamKey(streamId);

        opsForStream.add(record);
    }

    @Async
    public void publish(final Map<String, Object> objectMappedByStreamId) {
        objectMappedByStreamId.forEach(this::publish);
    }

}
