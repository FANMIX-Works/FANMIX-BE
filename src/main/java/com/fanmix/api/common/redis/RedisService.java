package com.fanmix.api.common.redis;

import static com.fanmix.api.common.exception.CommonErrorCode.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fanmix.api.common.exception.CommonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	public <T> Optional<T> get(String key, Class<T> type) {
		log.info("get data from redis with key: {}, type: {}", key, type.getName());
		try {
			String value = (String)redisTemplate.opsForValue().get(key);
			if (type == String.class) {
				return Optional.ofNullable(type.cast(value));
			} else {
				return Optional.ofNullable(objectMapper.readValue(value, type));
			}
		} catch (IllegalArgumentException e) {
			log.warn("value for key does not exist in redis");
			return Optional.empty();
		} catch (JsonProcessingException e) {
			log.error("error occurred while processing JSON", e);
			throw new CommonException(COMMON_JSON_PROCESSING_ERROR);
		}
	}

	public void set(String key, Object data) {
		log.info("set data in redis with key: {}, data: {}", key, data);
		try {
			String value = (data instanceof String) ? (String)data : objectMapper.writeValueAsString(data);
			redisTemplate.opsForValue().set(key, value);
		} catch (JsonProcessingException e) {
			log.error("error occurred while processing JSON", e);
			throw new CommonException(COMMON_JSON_PROCESSING_ERROR);
		}
	}

	public void setWithExpiration(String key, Object data, Long expiration) {
		log.info("set data in redis with key: {}, data: {}, expiration: {} milliseconds", key, data, expiration);
		try {
			String value = (data instanceof String) ? (String)data : objectMapper.writeValueAsString(data);
			redisTemplate.opsForValue().set(key, value, expiration, TimeUnit.MILLISECONDS);
		} catch (JsonProcessingException e) {
			log.error("error occurred while processing JSON", e);
			throw new CommonException(COMMON_JSON_PROCESSING_ERROR);
		}
	}

	public void delete(String key) {
		log.info("delete data from redis with key: {}", key);
		redisTemplate.delete(key);
	}

	public <T> Optional<T> hget(String key, String hashKey, Class<T> type) {
		log.info("hget data from redis with key: {}, hashKey: {}, type: {}", key, hashKey, type.getName());
		try {
			String value = (String)redisTemplate.opsForHash().get(key, hashKey);
			if (type == String.class) {
				return Optional.ofNullable(type.cast(value));
			} else {
				return Optional.ofNullable(objectMapper.readValue(value, type));
			}
		} catch (IllegalArgumentException e) {
			log.warn("value for key does not exist in redis");
			return Optional.empty();
		} catch (JsonProcessingException e) {
			log.error("error occurred while processing JSON", e);
			throw new CommonException(COMMON_JSON_PROCESSING_ERROR);
		}
	}

	public void hset(String key, String hashKey, Object data) {
		log.info("hset data in redis with key: {}, hashKey: {}, data: {}", key, hashKey, data);
		try {
			String value = (data instanceof String) ? (String)data : objectMapper.writeValueAsString(data);
			redisTemplate.opsForHash().put(key, hashKey, value);
		} catch (JsonProcessingException e) {
			log.error("error occurred while processing JSON", e);
			throw new CommonException(COMMON_JSON_PROCESSING_ERROR);
		}
	}

	/*
	hset 은 expire 을 설정할 수 없음
	public void hsetWithExpiration(String key, String hashKey,Object data, Long expiration) {
	}
	 */
}
