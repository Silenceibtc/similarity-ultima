package cn.edu.dlmu.backend.constant;

/**
 * RedisKey常量
 */
public interface RedisKey {
    String USER_HOMEPAGE_REDIS_KEY = "similarity:user:homepage:pageNum:%d:pageSize:%d:userId:%s";
    String PREHEAT_CACHE_LOCK_KEY = "similarity:user:preheatCache:lock";
}
